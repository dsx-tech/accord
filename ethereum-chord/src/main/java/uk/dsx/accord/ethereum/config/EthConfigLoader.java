package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.ConfigLoader;
import uk.dsx.accord.common.client.SSHClient;
import uk.dsx.accord.ethereum.EthCommonNode;
import uk.dsx.accord.ethereum.EthInstance;
import uk.dsx.accord.ethereum.EthInstanceContainer;
import uk.dsx.accord.ethereum.FilePath;
import uk.dsx.accord.ethereum.config.ChainConfig.Genesis;
import uk.dsx.accord.ethereum.crypto.Account;
import uk.dsx.accord.ethereum.crypto.CryptoUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

@Log4j2
public class EthConfigLoader implements ConfigLoader<EthInstanceContainer, DefaultConfiguration> {

    @Override
    public EthInstanceContainer loadConfig(String file, Class<? extends DefaultConfiguration> configClass) {
        try {
            log.info("Mapping config from {} to {}", file, configClass);

            String tempDir = "temp";
            String keyStoreDir = tempDir + "/keystore";
            FileUtils.cleanDirectory(new File(tempDir));

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

            int nodesCount = configuration.getInstances().stream()
                    .mapToInt(instanceConfig -> instanceConfig.getNodes().size())
                    .sum();

            List<String> nodeNames = configuration.getInstances().stream()
                    .flatMap(instanceConfig -> instanceConfig.getNodes().stream())
                    .map(NodeConfig::getName)
                    .collect(Collectors.toList());
            List<Account> accounts = CryptoUtils.generateAccounts(nodesCount, chainConfig.getInitialBalance(), keyStoreDir);
            List<FilePath> accountsFiles = getAccountsFiles(accounts);
            Map<String, Account> etherbases = mapEtherbase(accounts, nodeNames);


            Path genesis = generateGenesis(accounts, chainConfig, tempDir);
            // Nodes and instances creation
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
                        .nodeDir(workingDir + "/.ether-" + nodeConfig.getName())
                        .apiDir(workingDir)
                        .etherBaseAccount(etherbases.get(nodeConfig.getName()))
                        .nodeArgs(getNodeRunArgs(nodeConfig.getType(), chainConfig, etherbases.get(nodeConfig.getName())))
                        .accountFiles(accountsFiles)
                        .nodeFile(genesis)
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

    private String getNodeRunArgs(NodeType type, ChainConfig chainConfig, Account etherbase) {
        String comonOptions = chainConfig.getCommonOptions()
                .replace("{networkId}", String.valueOf(chainConfig.getChainId()));

        String minerOptions = chainConfig.getMinerOptions()
                .replace("{etherbase}", etherbase.getAddress());

        switch (type) {
            case OBSERVER:
                return comonOptions;
            case MINER:
                return comonOptions + " " + minerOptions;
            default:
                return comonOptions;
        }
    }

    private Path generateGenesis(List<Account> accounts, ChainConfig chainConfig, String genesisDir) {
        Genesis genesis = chainConfig.getGenesis();
        ChainConfig.Config config = genesis.getConfig();

        Integer networkId = chainConfig.getChainId();
        config.setChainId(networkId);

        CryptoUtils.addAccountsToGenesis(accounts, genesis);
        Path genesisPath = CryptoUtils.createGenesisFile(genesis, genesisDir);

        return genesisPath;
    }

    private List<FilePath> getAccountsFiles(List<Account> accounts) {
        return accounts.stream().map(Account::getKeyFile)
                .map(keyFile -> new FilePath(keyFile, "keystore/" + Paths.get(keyFile).getFileName().toString()))
                .collect(Collectors.toList());
    }

    private List<Path> mapStringsIntoPaths(List<String> stringPaths) {
        if (isNull(stringPaths)) {
            return new ArrayList<>();
        }
        return stringPaths.stream().map(path -> Paths.get(path)).collect(Collectors.toList());
    }

    private List<FilePath> mapStringsIntoFileBindings(List<String> stringPaths) {
        if (isNull(stringPaths)) {
            return new ArrayList<>();
        }
        return stringPaths.stream().map(path -> FilePath.get(path)).collect(Collectors.toList());
    }


    private Map<String, Account> mapEtherbase(List<Account> accounts, List<String> nodeNames) {
        int size = Integer.min(accounts.size(), nodeNames.size());
        return IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toMap(nodeNames::get, accounts::get));
    }

}
