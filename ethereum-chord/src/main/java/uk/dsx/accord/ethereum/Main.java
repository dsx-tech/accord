package uk.dsx.accord.ethereum;

import uk.dsx.accord.ethereum.config.DefaultConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws InstantiationException, InterruptedException {
        String user = "ec2-user";
        int port = 22;
        String firstIp = "";
        String key = "ethereum-chord/src/main/resources/ethereum/eth-new.pem";
        List<String> prepareEnvCommands = Arrays.asList(
                "sudo yum update -y",
                "sudo yum install -y docker",
                "sudo service docker start",
                "sudo groupadd docker",
                "sudo gpasswd -a $USER docker",
                "sudo usermod -aG docker $USER"
        );
        List<Path> files = Stream.of(
                "ethereum-chord/src/main/resources/ethereum/init.sh",
                "ethereum-chord/src/main/resources/ethereum/genesis.json"
        ).map(Paths::get).collect(Collectors.toList());
//        EthInstance instance = new EthInstance(user, firstIp, port, key, prepareEnvCommands, files);
//        instance.uploadFiles("boot");
        EthInstanceManager manager = new EthInstanceManager();
        manager.withConfig("", DefaultConfiguration.class);
//        manager.addInstance(instance).run();
//        manager.terminate();
    }

}
