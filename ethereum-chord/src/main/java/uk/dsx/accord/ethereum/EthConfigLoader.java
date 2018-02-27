package uk.dsx.accord.ethereum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.ConfigLoader;
import uk.dsx.accord.common.client.SSHClient;
import uk.dsx.accord.ethereum.config.DefaultConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class EthConfigLoader implements ConfigLoader<EthInstanceContainer, DefaultConfiguration> {

    @Override
    public EthInstanceContainer loadConfig(String file, Class<? extends DefaultConfiguration> configClass) {
        try {

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            DefaultConfiguration configuration = mapper.readValue(new File(file), configClass);

            List<Path> allNodesFiles = mapStringsIntoPaths(configuration.getAllNodeFiles());

            List<EthInstance> instances = configuration.getInstances().stream().map(instanceConfig -> {

                final String user_dir = "/home/ec2-user";
                final String shared_dir = user_dir + "/shared_dir";


                Client client = SSHClient.builder()
                        .user(instanceConfig.getUser())
                        .privateKey(instanceConfig.getFingerprintPath())
                        .host(instanceConfig.getIp())
                        .port(instanceConfig.getPort())
                        .build();

                //Nodes creation
                List<EthNode> nodes = instanceConfig.getNodes().stream().map(nodeConfig -> EthNode.builder()
                        .name(nodeConfig.getName())
                        .ip(instanceConfig.getIp())
                        .port(nodeConfig.getPort())
                        .parentInstance(instanceConfig.getName())
                        .type(nodeConfig.getType())
                        .client(client)
                        .dir(shared_dir + "/" + nodeConfig.getName())
                        .nodeFiles(mapStringsIntoPaths(nodeConfig.getNodeFiles()))
                        .nodeFiles(mapStringsIntoPaths(instanceConfig.getInstanceSpecifiedNodesFiles()))
                        .nodeFiles(allNodesFiles)
                        .build()).collect(Collectors.toList());

                return EthInstance.builder()
                        .user(instanceConfig.getUser())
                        .ip(instanceConfig.getIp())
                        .port(instanceConfig.getPort())
                        .fingerprintPath(instanceConfig.getFingerprintPath())
                        .dir(shared_dir)
                        .prepareEnvCommands(instanceConfig.getPrepareEnvCommands())
                        .instanceFiles(mapStringsIntoPaths(instanceConfig.getInstanceFiles()))
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

    private List<Path> mapStringsIntoPaths(List<String> stringPaths) {
        if (isNull(stringPaths)) {
            return new ArrayList<>();
        }
        return stringPaths.stream().map(path -> Paths.get(path)).collect(Collectors.toList());
    }
}
