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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EthCommonNode {

    private String name;

    private String ip;

    @NonNull
    private Client client;

    private Integer port;

    private Integer rpcPort;

    private String parentInstance;

    private NodeType type;

    @Singular
    private List<Path> nodeFiles;

    private String nodeDir;

    private String apiDir;

    private String nodeArgs;

    private State state = State.NEW;

    @NonNull
    private List<String> nodePeers;

    public void run() {
        exec("/accord run node ", nodeArgs);
    }

    public void kill() {
        exec("/accord kill node");
    }

    public void wipe() {
        exec("/accord wipe node");
    }

    public void addPeer(String peer) {
        exec("/accord add peer", peer);
    }

    public void addPeers(List<String> peers) {
        peers.forEach(this::addPeer);
    }

    public List<PortBinding> requestPorts() {
        return readConsoleOutput(exec("/accord get ports"))
                .filter(s -> s.matches(".* -> .*"))
                .map(s -> s.replaceAll("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:", ""))
                .map(s -> {
                    //Parse ports
                    String[] ports = s.split(" -> ");

                    String[] exposedPortAndProto = ports[0].split("/");
                    String exposedPort = exposedPortAndProto[0];
                    String protocol = exposedPortAndProto[1];

                    String[] hostPortAndProto = ports[1].split("/");
                    String hostPort = hostPortAndProto[0];


                    return PortBinding.builder()
                            .hostPort(hostPort)
                            .exposedPort(exposedPort)
                            .protocol(protocol)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public String requestPeers() {
        return readConsoleOutput(exec("/accord get peers"))
                .collect(Collectors.joining("\n"));
    }

    public String requestEnode() {
        return readConsoleOutput(exec("/accord get enode"))
                .map(s -> s.replace("\"", ""))
                .filter(s -> s.matches("^enode://.*"))
                .collect(Collectors.joining("\n"));
    }

    public String requestLogs() {
        return readConsoleOutput(exec("/accord get logs"))
                .collect(Collectors.joining("\n"));
    }

    public void uploadFile(InputStream source, String target) {
        client.sendFile(source, target);
    }

    public void uploadFile(String source, String target) {
        client.sendFile(source, target);
    }

    public InputStream downloadFile(String path) {
        return client.getFile(path);
    }

    public void uploadFiles(String nodeDir) {
        nodeFiles.forEach(path -> uploadFile(path.toString(), nodeDir + "/" + path.getFileName().toString()));
    }

    private InputStream exec(String command) {
        return exec(command, "");
    }

    private InputStream exec(String command, String params) {
        return client.exec(apiDir + command + " " + name + " " + params);
    }

    private Stream<String> readConsoleOutput(InputStream command) {
        return new BufferedReader(new InputStreamReader(command))
                .lines();
    }
}
