package uk.dsx.accord.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import uk.dsx.accord.common.ConfigurationLoader;
import uk.dsx.accord.ethereum.config.DefaultConfiguration;
import uk.dsx.accord.ethereum.config.NodeType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EthConfigLoader implements ConfigurationLoader<DefaultConfiguration> {

    List<EthInstance> instances;
    //EXAMPLE: maybe we want group nodes or instances
    List<EthNode> bootNodes;
    List<EthNode> commonNodes;

    //OR we want represent instances in other form
    Map<NodeType, EthNode> nodeMap;

    // OR we wanna set parrentInstance in node
    // OR we wanna check that all nodes have different names (if needed)

    // OR WE CAN FAKE SSH clients and not change config (FakeSSHD now in jcabi)

    // MANY BAD CODE mapping libraries could help

    @Override
    public void loadConfig(String confFile, Class<? extends DefaultConfiguration> defaultConfigurationClass) {
        try {

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            DefaultConfiguration configuration = mapper.readValue(new File(confFile), defaultConfigurationClass);

            List<Path> allNodesFiles = mapStringsIntoPaths(configuration.getAllNodeFiles());

            instances = configuration.getInstances().stream().map(instanceConfig -> {
                //Nodes creation
                List<EthNode> nodes = instanceConfig.getNodes().stream().map(nodeConfig -> EthNode.builder()
                        .name(nodeConfig.getName())
                        .parentInstance(instanceConfig.getName())
                        .type(nodeConfig.getType())
                        .nodeFiles(mapStringsIntoPaths(nodeConfig.getNodeFiles()))
                        .nodeFiles(mapStringsIntoPaths(instanceConfig.getInstanceSpecifiedNodesFiles()))
                        .nodeFiles(allNodesFiles)
                        .build()).collect(Collectors.toList());

                return EthInstance.builder()
                        .user(instanceConfig.getUser())
                        .ip(instanceConfig.getIp())
                        .port(instanceConfig.getPort())
                        .fingerprintPath(instanceConfig.getFingerprintPath())
                        .prepareEnvCommands(instanceConfig.getPrepareEnvCommands())
                        .instanceFiles(mapStringsIntoPaths(instanceConfig.getInstanceFiles()))
                        .nodes(nodes)
                        .build();

            }).collect(Collectors.toList());
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Path> mapStringsIntoPaths(List<String> stringPaths) {
        return stringPaths.stream().map(path -> Paths.get(path)).collect(Collectors.toList());
    }
}
