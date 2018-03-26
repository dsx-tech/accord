package uk.dsx.accord.ethereum;

import lombok.*;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.enums.State;
import uk.dsx.accord.ethereum.config.NodeType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EthNode {

    private String name;

    private String ip;

    private Client client;

    private Integer port;

    private Integer rpcPort;

    private String parentInstance;

    private NodeType type;

    @Singular
    private List<Path> nodeFiles;

    private String dir;

    private State state = State.NEW;

    public void run(String bootNode) {

        client.connect();

        String docker_run = DockerCommand.builder()
                .command("run")
                .param("-idt")
                .name(name)
                .port(port + ":30303")
                .port(rpcPort + ":8101")
                .volume(dir + ":" + "/node_dir/")
                .variable("BOOTNODES=\'" + bootNode + "\'")
                .variable("NODE_DIR=/node_dir")
                .container("chai0103/eth")
                .entryPoint("sh /node_dir/init.sh")
                .log_file(" >> all.log")
                .build().toString();

        System.out.println(docker_run);

        client.mkdir(dir);
        uploadFiles(dir);
        client.exec(docker_run);

        state = State.RUNING;
    }

    public void uploadFiles(String nodeDir) {
        nodeFiles.forEach(path -> uploadFile(path.toString(), nodeDir + "/" + path.getFileName().toString()));
    }

    public void uploadFile(String source, String target) {
        client.send(source, target);
    }

    public String getEnode() {
        String enode = "";
        while (enode.isEmpty()) {
            try (BufferedReader enodeReader = new BufferedReader(new InputStreamReader(downloadFile(dir + "/enode")))) {
                enode = enodeReader.readLine()
                        .replace("[::]", ip)
                        .replace(":30303", ":" + port);
            } catch (Throwable ignored) {
//                ignored.printStackTrace();
            }

        }
        return enode;
    }

    public InputStream downloadFile(String path) {
        return client.get(path);
    }

}
