package uk.dsx.accord.ethereum;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InstantiationException, InterruptedException {
        String user = "ec2-user";
        int port = 22;
        String firstIp = "54.163.191.46";
        String key = "ethereum-chord/src/main/resources/ethereum/eth-new.pem";
        List<String> prepareEnvCommands = Arrays.asList(
                "sudo yum update -y",
                "sudo yum install -y docker",
                "sudo service docker start",
                "sudo groupadd docker",
                "sudo gpasswd -a $USER docker",
                "sudo usermod -aG docker $USER"
        );
        EthInstance instance = new EthInstance(user, firstIp, port, key, prepareEnvCommands);
        EthInstanceManager manager = new EthInstanceManager();
        manager.addInstance(instance).run();
        manager.terminate();
    }

}
