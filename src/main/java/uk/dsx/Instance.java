package uk.dsx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.util.Pair;
import uk.dsx.driver.EnvironmentManager;
import uk.dsx.driver.SSHEnvironmentManager;
import uk.dsx.node.AbstractNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alexander on 25.11.17.
 */
public class Instance {
    protected String user;
    protected String ip;
    protected EnvironmentManager client;
    protected List<? extends AbstractNode> nodes = new ArrayList<>();

    @JsonCreator
    public Instance(@JsonProperty("user") String user,
                    @JsonProperty("ip") String ip,
                    @JsonProperty("nodes") List<? extends AbstractNode> nodes,
                    @JsonProperty("keyPath") String keyPath){
        this.user = user;
        this.ip = ip;
        this.nodes = nodes;
        if (ip == "localhost" || ip == "192.168.0.1"){
        }
        else{
            client = new SSHEnvironmentManager(keyPath);
        }
    }

    //update packages, install docker, set permissions
    public void prepareEnvironment(List<String> commands){
        client.executeCommands(commands, user, ip);
    }

    //create folders as a scheme
    protected void prepareNodesFolders(String basePath){
        List<String> commandsForFolders = nodes.stream().flatMap(node ->
                Arrays.asList(
                        "mkdir " + basePath + node.getDockerName(),
                        "mkdir " + basePath + node.getDockerName() + "/shared",
                        "mkdir " + basePath + node.getDockerName() + "/driver"
                ).stream()).collect(toList());
        client.executeCommands(commandsForFolders, user, ip);
    }

    //TODO set files via json
    //upload files to folders
    protected void uploadFiles(List<String> sharedFiles, List<String> driverFiles){
        Path p = Paths.get("/lalala");
        nodes.stream().flatMap(node ->
        Arrays.asList(
                generatePairs(sharedFiles, node.getDockerName(), FolderType.Shared).stream(),
                generatePairs(driverFiles, node.getDockerName(), FolderType.Shared).stream()
        ).stream()).forEach(pair -> client.uploadFile(pair.getKey(), pair.getValue(), user, ip));
    }

    public void startNode(int index) {
        client.executeCommands(
                Arrays.asList(//TODO use index for verbose nodes, make doc for verbose files
                        "docker run --name root_node -p 30303:30303 -p 8101:8101 -v '/home/ec2-user/eth/:/eth' chai0103/eth"
                ), user, ip);
    }

    public boolean hasRoot(){
        return nodes.stream().filter(node -> node.getDockerName().contains("root")).findFirst().isPresent();
    }

    private List<Pair<Path, Path>> generatePairs(List<String> from, String dockerName, FolderType type){
        return from.stream().map(x -> generatePair(x, dockerName, type)).collect(toList());
    }

    private Pair<Path, Path> generatePair(String from, String dockerName, FolderType type){
        Path pathFrom = Paths.get(from);
        return new Pair<Path, Path>(pathFrom, Paths.get("/environment/" + dockerName + type + pathFrom.getFileName()));
    }

    protected enum FolderType{
        Shared{
            @Override
            public String toString() {
                return "/shared/";
            }
        },
        Driver{
            @Override
            public String toString() {
                return "/driver/";
            }
        }
    }
}
