package uk.dsx.accord.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import uk.dsx.accord.common.ConfigurationLoader;
import uk.dsx.accord.common.client.SSHClient;
import uk.dsx.accord.ethereum.config.DefaultConfiguration;
import uk.dsx.accord.ethereum.config.NodeType;

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
    public void loadConfig(String confFile, Class<DefaultConfiguration> defaultConfigurationClass) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            DefaultConfiguration configuration = mapper.readValue(confFile, defaultConfigurationClass);

            List<Path> allNodesFiles = mapStringsIntoPaths(configuration.getAllNodeFiles());

            instances = configuration.getInstances().stream().map(instanceConfig -> {

                //Instance mapping
                String instanceName = instanceConfig.getName();
                String user = instanceConfig.getUser();
                String ip = instanceConfig.getIp();
                int port = instanceConfig.getPort();
                String keyPath = instanceConfig.getFingerprintPath();
                List<String> prepareEnvCommands = instanceConfig.getPrepareEnvCommands();
                List<Path> instanceFiles = mapStringsIntoPaths(instanceConfig.getInstanceFiles());
                List<Path> instaceNodeFiles = mapStringsIntoPaths(instanceConfig.getInstanceSpecifiedNodesFiles());
                SSHClient client = new SSHClient(user, keyPath, ip, port);

                //Nodes creation
                List<EthNode> nodes = instanceConfig.getNodes().stream().map(nodeConfig -> {
                    String nodeName = nodeConfig.getName();
                    NodeType nodeType = nodeConfig.getType();
                    List<Path> nodeFiles = mapStringsIntoPaths(nodeConfig.getNodeFiles());
                    nodeFiles.addAll(instaceNodeFiles);
                    nodeFiles.addAll(allNodesFiles);
                    return new EthNode(nodeName, instanceName, nodeType, nodeFiles);
                }).collect(Collectors.toList());

                return new EthInstance(instanceName, ip, port, keyPath, prepareEnvCommands, instanceFiles, client, nodes);

            }).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Path> mapStringsIntoPaths(List<String> stringPaths) {
        return stringPaths.stream().map(path -> Paths.get(path)).collect(Collectors.toList());
    }
}
