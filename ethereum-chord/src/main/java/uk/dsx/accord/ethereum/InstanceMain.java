package uk.dsx.accord.ethereum;

import uk.dsx.accord.common.client.SSHClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class InstanceMain {

    private String user;
    private String key;
    private String host;
    private int port;

    private SSHClient client;
    private int nodeCount = 2;

    public InstanceMain(String user, String password, String host, int port) {
        try {
            this.user = user;
            this.key = password;
            this.host = host;
            this.port = port;
            this.client = new SSHClient(user, password, host, port);
            client.connect();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void start() throws InstantiationException, InterruptedException {

        client.mkdir("shared_dir");
        client.cd("shared_dir");
        client.send("ethereum-chord/src/main/resources/ethereum/start_env.sh", "start_env.sh");

//        client.exec("chmod start_env.sh");
        client.exec("bash start_env.sh");

        nodeRun(0, "dummy");

        String enode = "";
        while (enode.isEmpty()) {
//            System.out.println(client.ls("./node0/"));
            try (BufferedReader enodeReader = new BufferedReader(new InputStreamReader(client.get("node0/enode")))) {
                enode = enodeReader.lines().collect(Collectors.joining(","));
            } catch (Throwable ignored) {
                Thread.sleep(1000);
            } finally {
                System.out.println(enode);
            }

        }
        enode = enode.replace("[::]", host);
        System.out.println(enode);
//        enode = enode.replace(":303303", "");
        for (int nodeNumber = 1; nodeNumber < nodeCount; nodeNumber++) nodeRun(nodeNumber, enode);

        while (true) {
            Thread.sleep(10000);
        }

    }


    public void cleanAll() {
        try {
            client.exec("sudo rm -rf /home/ec2-user/shared_dir");
            client.exec("docker rm -f $(docker ps -a -q)");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void nodeRun(int nodeNumber, String bootNode) {
        String user_dir = "/home/ec2-user";
        String shared_dir = "/shared_dir";

        String node_name = "node" + nodeNumber;
        String node_dir = "/" + node_name;
        String absouluteNodeDir = user_dir + shared_dir + node_dir;
        int dockerPort = 30303 + nodeNumber;

        String nameParam = " --name " + node_name;
        String portParam = " -p " + dockerPort + ":30303";
        String volumeParam = " -v " + user_dir + shared_dir + node_dir + ":" + "/node_dir/";
        String bootNodeParam = " -e BOOTNODES=\'" + bootNode + "\'";
        String nodeDirParam = " -e NODE_DIR=/node_dir";
        String entryPoint = " sh /node_dir/init.sh";
//            String log_file = " > " + node_name + ".log";
        String log_file = " >> all.log";

        String docker_run = "docker run -idt"
                + nameParam + portParam + volumeParam + bootNodeParam + nodeDirParam
                + " chai0103/eth"
                + entryPoint
                + log_file;
        System.out.println(docker_run);
        client.mkdir(node_name);
        client.send("ethereum-chord/src/main/resources/ethereum/init.sh", node_name + "/init.sh");
        client.send("ethereum-chord/src/main/resources/ethereum/genesis.json", node_name + "/genesis.json");
        client.exec(docker_run);
    }

    public static void main(String[] args) throws InstantiationException, InterruptedException {
        String user = "ec2-user";
        int port = 22;
        String host = "*";
        String key = "*";
        InstanceMain instanceMain = new InstanceMain(user, key, host, port);
        instanceMain.cleanAll();

        instanceMain.start();

    }
}
