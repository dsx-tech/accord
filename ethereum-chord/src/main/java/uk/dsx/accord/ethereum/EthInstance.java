package uk.dsx.accord.ethereum;

import lombok.Data;
import uk.dsx.accord.common.AbstractInstance;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.client.SSHClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

@Data
public class EthInstance extends AbstractInstance {

    private final String user_dir = "/home/ec2-user";
    private final String shared_dir = user_dir + "/shared_dir";

    private String name;

    private String ip;

    private int port;

    private String fingerprintPath;

    private List<String> prepareEnvCommands;

    private List<Path> instanceFiles;

    private Client client;

    private List<EthNode> nodes;

    private List<String> commands;
    private Map<String, String> nodeDirs = new HashMap<>();
    private Map<String, String> nodePorts = new HashMap<>();

    public int nodeCount;

    public EthInstance(String user, String ip, int port, String fingerprintPath, List<String> prepareEnvCommands) {
        this.name = user;
        this.ip = ip;
        this.port = port;
        this.fingerprintPath = fingerprintPath;
//        this.prepareEnvCommands = prepareEnvCommands.stream().collect(Collectors.joining(" && "));
        this.prepareEnvCommands = prepareEnvCommands;
        this.client = new SSHClient(user, fingerprintPath, ip, port);
        this.commands = new ArrayList<>();
    }

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
        // Maybe Map<String, String> params??
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
        uploadFile("ethereum-chord/src/main/resources/ethereum/init.sh", node_dir + "/init.sh");
        uploadFile("ethereum-chord/src/main/resources/ethereum/genesis.json", node_dir + "/genesis.json");
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

}
