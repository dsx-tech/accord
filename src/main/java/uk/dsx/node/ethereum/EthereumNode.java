package uk.dsx.node.ethereum;

import uk.dsx.driver.EnvironmentManager;
import uk.dsx.node.AbstractNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexander on 22.11.17.
 */
public class EthereumNode<T extends EnvironmentManager> extends AbstractNode {
    private T manager;

    //TODO make commands via json
    @Override
    public void prepareEnvironment(List<String> sharedFiles) {
        List<String> commands =
        Arrays.asList(
            "sudo mkdir /environment",
            String.format("sudo mkdir /environment/%s/shared", dockerName),
            String.format("sudo mkdir /environment/%s/driver", dockerName),
            String.format("sudo chown -R %s:%s /environment", user, user),
            "sudo apt-get update",
            "sudo apt-get install docker");
        manager.executeCommands(commands, user, ip);
        System.out.println(sharedFiles.get(0));
        List<Path> files =
            Arrays.asList(
                Paths.get(sharedFiles.get(0)));
        manager.uploadFiles(files, user, ip);
    }

    @Override
    public void startNode() {
        List<String> commands =
                Arrays.asList(
                        "cd /environment",
                        "docker build . -t sandbox:5",
                        String.format("docker run --name %s -P -v '/environment/%s/shared:/testenv' sandbox:5", dockerName, dockerName));
        manager.executeCommands(commands, user, ip);
    }

    public void setManager(T manager){
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "EthereumNode<"+manager.getClass().getSimpleName()+">{" +
                super.toString() +
                '}';
    }
}
