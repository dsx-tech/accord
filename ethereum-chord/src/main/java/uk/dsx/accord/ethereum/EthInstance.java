package uk.dsx.accord.ethereum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import uk.dsx.accord.common.AbstractInstance;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.client.SSHClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class EthInstance extends AbstractInstance {

    private final String user_dir = "/home/ec2-user";
    private final String shared_dir = user_dir + "/shared_dir";

    private String user;

    private String ip;

    private int port;

    private String fingerprintPath;

    @Singular
    private List<String> prepareEnvCommands;

    @Singular
    private List<Path> instanceFiles;

    private Client client;

    @Singular
    private List<EthNode> nodes;

    private List<String> commands;
    private Map<String, String> nodeDirs = new HashMap<>();
    private Map<String, String> nodePorts = new HashMap<>();

    public int nodeCount;

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EthInstance prepare() {
        addCommands(prepareEnvCommands);
        return this;
    }

    @Override
    public EthInstance run() {
        if (client == null) {
            this.client = new SSHClient(user, fingerprintPath, ip, port);
        }

        client.connect();
        return this;
    }

    @Override
    public EthInstance clean() {
        addCommand("sudo rm -rf " + shared_dir);
        addCommand("sudo docker rm -f $(docker ps -a -q)");
        return this;
    }

    @Override
    public void terminate() {
        client.close();
    }

    @Override
    public EthInstance addCommand(String command) {
        commands.add(command);
        return this;
    }

    @Override
    public EthInstance addCommands(Collection<String> commands) {
        commands.addAll(commands);
        return this;
    }

    @Override
    public void exec() {
        commands.forEach(command -> client.exec(command));
        client.reconnect();
    }

    @Override
    public void uploadFile(String source, String target) {
        client.send(source, target);
    }

    @Override
    public InputStream downloadFile(String path) {
        return client.get(path);
    }


    public void runNode(String nodeName, String bootNode, int port) {
        String node_dir = shared_dir + "/" + nodeName;
        nodeDirs.put(nodeName, node_dir);
        nodePorts.put(nodeName, String.valueOf(port));

        String docker_run = DockerCommand.builder()
                .command("run")
                .param("-idt")
                .name(nodeName)
                .port(port + ":30303")
                .volume(node_dir + ":" + "/node_dir/")
                .variable("BOOTNODES=\'" + bootNode + "\'")
                .variable("NODE_DIR=/node_dir")
                .container("chai0103/eth")
                .entryPoint("sh /node_dir/init.sh")
                .log_file(" >> all.log")
                .build().toString();

        System.out.println(docker_run);

        client.mkdir(node_dir);
        uploadFiles(node_dir);
        client.exec(docker_run);
    }

    public String getEnode(String nodeName) {
        String enode = "";
        while (enode.isEmpty()) {
            try (BufferedReader enodeReader = new BufferedReader(new InputStreamReader(downloadFile(nodeDirs.get(nodeName) + "/enode")))) {
                enode = enodeReader.readLine()
                        .replace("[::]", ip)
                        .replace(":30303", ":" + nodePorts.get(nodeName));
            } catch (Throwable ignored) {
//                ignored.printStackTrace();
            }

        }
        return enode;
    }

    public void uploadFiles(String nodeDir) {
        instanceFiles.stream().forEach(path -> {
            uploadFile(path.toString(), nodeDir + "/" + path.getFileName().toString());
        });
    }

}
