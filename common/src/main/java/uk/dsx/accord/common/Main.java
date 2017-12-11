package uk.dsx.accord.common;

import uk.dsx.accord.common.client.SSHClient;

public class Main {

    public static void main(String[] args) throws InstantiationException {
        String user = "ec2-user";
        int port = 22;
        String host = "ec2-54-89-6-199.compute-1.amazonaws.com";
        String key = "src/main/resources/ethereum/eth-new.pem";
        SSHClient client = new SSHClient(user, key, host, port)
                .connect()
                .send("src/main/resources/ethereum/init.sh", "init.sh")
                .send("src/main/resources/ethereum/start_env.sh", "start_env.sh")
                .close();
    }

}
