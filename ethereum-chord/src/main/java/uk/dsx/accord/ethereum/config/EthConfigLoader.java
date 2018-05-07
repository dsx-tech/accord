package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.ConfigLoader;
import uk.dsx.accord.common.client.DummyClient;
import uk.dsx.accord.ethereum.EthCommonNode;
import uk.dsx.accord.ethereum.EthInstance;
import uk.dsx.accord.ethereum.EthInstanceContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.Objects.isNull;

@Log4j2
public class EthConfigLoader implements ConfigLoader<EthInstanceContainer, DefaultConfiguration> {

    @Override
    public EthInstanceContainer loadConfig(String file, Class<? extends DefaultConfiguration> configClass) {
        try {
            log.info("Mapping config from {} to {}", file, configClass);

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            DefaultConfiguration configuration = mapper.readValue(new File(file), configClass);


            List<Path> allNodesFiles = mapStringsIntoPaths(configuration.getAllNodeFiles());

            List<String> allSharedNodes = configuration.getInstances().stream()
                    .flatMap(instanceConfig -> instanceConfig.getNodes().stream())
                    .filter(nodeConfig -> nodeConfig.getPeers() == null || nodeConfig.getPeers().size() == 0)
                    .map(NodeConfig::getName)
                    .distinct()
                    .collect(Collectors.toList());

            ChainConfig chainConfig = configuration.getChainConfig();

            long nodesCount = configuration.getInstances().stream()
                    .mapToLong(instanceConfig -> instanceConfig.getNodes().size())
                    .sum();

            Path genesis = generateGenesis(nodesCount, chainConfig);

            List<EthInstance> instances = configuration.getInstances().stream().map(instanceConfig -> {

                final String workingDir = instanceConfig.getWorkingDir();

//                Client client = SSHClient.builder()
//                        .user(instanceConfig.getUser())
//                        .privateKey(instanceConfig.getFingerprintPath())
//                        .host(instanceConfig.getIp())
//                        .port(instanceConfig.getPort())
//                        .build();

                Client client = DummyClient.builder()
                        .user(instanceConfig.getUser())
                        .privateKey(instanceConfig.getFingerprintPath())
                        .host(instanceConfig.getIp())
                        .port(instanceConfig.getPort())
                        .build();

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
                        .nodeArgs(getNodeRunArgs(nodeConfig.getType(), chainConfig))
                        .nodeFile(genesis)
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
                        .instanceFile(genesis)
                        .instanceFiles(mapStringsIntoPaths(instanceConfig.getInstanceFiles()))
                        .postInitCommands(instanceConfig.getPostInitCommands())
                        .postInitFiles(mapStringsIntoPaths(instanceConfig.getPostInitFiles()))
                        .nodes(nodes)
                        .build();

            }).collect(Collectors.toList());

            log.info("Config mapped");
            return new EthInstanceContainer(instances);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getNodeRunArgs(NodeType type, ChainConfig chainConfig) {
        switch (type) {
            case OBSERVER:
                return chainConfig.getCommonOptions();
            case MINER:
                return chainConfig.getCommonOptions() + " " + chainConfig.getMinerOptions();
            default:
                return chainConfig.getCommonOptions();
        }
    }

    private Path generateGenesis(long nodesCount, ChainConfig chainConfig) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ChainConfig.Genesis genesis = chainConfig.getGenesis();
            Map<String, ChainConfig.Alloc> allocMap = genesis.getAlloc();

//             ChainConfig.Alloc generation
            if (allocMap.isEmpty()) {
                DecimalFormat forty = new DecimalFormat(StringUtils.repeat("0", 40));
                LongStream.rangeClosed(1, nodesCount)
//                        .mapToObj(seed -> RandomStringUtils.random(40, true, true))
                        .mapToObj(seed -> forty.format(seed))
                        .map(String::toLowerCase)
                        .forEach(id -> allocMap.put(id, new ChainConfig.Alloc(chainConfig.getInitialBalance())));
            }

            File file = new File("temp/genesis.json");
            file.getParentFile().mkdirs();
            file.createNewFile();
            file.deleteOnExit();

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, chainConfig.getGenesis());

            return Paths.get("temp/genesis.json");
        } catch (IOException e) {
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
