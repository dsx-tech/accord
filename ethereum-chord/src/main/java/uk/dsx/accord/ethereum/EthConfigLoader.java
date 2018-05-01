package uk.dsx.accord.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.ConfigLoader;
import uk.dsx.accord.common.client.SSHClient;
import uk.dsx.accord.ethereum.config.DefaultConfiguration;
import uk.dsx.accord.ethereum.config.NodeConfig;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class EthConfigLoader implements ConfigLoader<EthInstanceContainer, DefaultConfiguration> {

    @Override
    public EthInstanceContainer loadConfig(String file, Class<? extends DefaultConfiguration> configClass) {
        try {

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            DefaultConfiguration configuration = mapper.readValue(new File(file), configClass);

            List<Path> allNodesFiles = mapStringsIntoPaths(configuration.getAllNodeFiles());

            List<String> allSharedNodes = configuration.getInstances().stream()
                    .flatMap(instanceConfig -> instanceConfig.getNodes().stream())
                    .filter(nodeConfig -> nodeConfig.getPeers() == null || nodeConfig.getPeers().size() == 0)
                    .map(NodeConfig::getName)
                    .distinct()
                    .collect(Collectors.toList());

            List<EthInstance> instances = configuration.getInstances().stream().map(instanceConfig -> {

                final String workingDir = instanceConfig.getWorkingDir();

                Client client = SSHClient.builder()
                        .user(instanceConfig.getUser())
                        .privateKey(instanceConfig.getFingerprintPath())
                        .host(instanceConfig.getIp())
                        .port(instanceConfig.getPort())
                        .build();

//                Client client = DummyClient.builder()
//                        .user(instanceConfig.getUser())
//                        .privateKey(instanceConfig.getFingerprintPath())
//                        .host(instanceConfig.getIp())
//                        .port(instanceConfig.getPort())
//                        .build();

                //Nodes creation
                List<EthCommonNode> nodes = instanceConfig.getNodes().stream().map(nodeConfig -> EthCommonNode.builder()
                        .name(nodeConfig.getName())
                        .ip(instanceConfig.getIp())
                        .port(nodeConfig.getPort())
                        .rpcPort(nodeConfig.getRpcPort())
                        .parentInstance(instanceConfig.getName())
                        .type(nodeConfig.getType())
                        .client(client)
                        .nodeDir(workingDir + "/.ethereum-" + nodeConfig.getName())
                        .apiDir(workingDir)
                        .nodeFiles(mapStringsIntoPaths(nodeConfig.getNodeFiles()))
                        .nodeFiles(mapStringsIntoPaths(instanceConfig.getInstanceSpecifiedNodesFiles()))
                        .nodeFiles(mapStringsIntoPaths(instanceConfig.getInstanceFiles()))
                        .nodeFiles(allNodesFiles)
                        .nodePeers(nodeConfig.getPeers().isEmpty() ? allSharedNodes : nodeConfig.getPeers())
                        .build()).collect(Collectors.toList());

                return EthInstance.builder()
                        .user(instanceConfig.getUser())
                        .ip(instanceConfig.getIp())
                        .client(client)
                        .port(instanceConfig.getPort())
                        .fingerprintPath(instanceConfig.getFingerprintPath())
                        .dir(workingDir)
                        .commands(new ArrayList<>())
                        .prepareEnvCommands(instanceConfig.getPrepareEnvCommands())
                        .instanceFiles(mapStringsIntoPaths(instanceConfig.getInstanceFiles()))
                        .postInitCommands(instanceConfig.getPostInitCommands())
                        .postInitFiles(mapStringsIntoPaths(instanceConfig.getPostInitFiles()))
                        .nodes(nodes)
                        .build();

            }).collect(Collectors.toList());

            System.out.println("Mapped");
            return new EthInstanceContainer(instances);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private FileSystem initFileSystem(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            try {
                return FileSystems.newFileSystem(uri, env);
            } catch (IllegalArgumentException e2) {
                return FileSystems.getDefault();
            }
        } catch (IllegalArgumentException e2) {
            return FileSystems.getDefault();
        }
    }

    private List<Path> mapStringsIntoPaths(List<String> stringPaths) {
        if (isNull(stringPaths)) {
            return new ArrayList<>();
        }
        return stringPaths.stream().map(path -> {
            Path path1 = Paths.get(path);
            try {
                URI uri = path1.toUri();
                FileSystem zipfs = initFileSystem(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path1;
        }).collect(Collectors.toList());
    }
}
