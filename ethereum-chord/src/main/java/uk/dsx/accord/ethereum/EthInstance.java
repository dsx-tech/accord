package uk.dsx.accord.ethereum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.dsx.accord.common.AbstractInstance;
import uk.dsx.accord.common.Client;

import java.nio.file.Path;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthInstance extends AbstractInstance {

    private String name;

    private String ip;

    private int port;

    private String fingerprintPath;

    private List<String> prepareEnvCommands;

    private List<Path> instanceFiles;

    private Client client;

    private List<EthNode> nodes;


    @Override
    public void start() {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void run() {

    }

    @Override
    public void clean() {

    }

    @Override
    public void terminate() {

    }

    @Override
    public void addCommand(String command) {

    }

    @Override
    public void addCommands(List<String> commands) {

    }

    @Override
    public void exec() {

    }

    @Override
    public void uploadFiles(String path, List<String> files) {

    }

    @Override
    public List<String> downloadFiles(List<String> path) {
        return null;
    }
}
