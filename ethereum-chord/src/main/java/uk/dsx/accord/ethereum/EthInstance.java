package uk.dsx.accord.ethereum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import uk.dsx.accord.common.AbstractInstance;
import uk.dsx.accord.common.Client;
import uk.dsx.accord.common.client.SSHClient;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class EthInstance extends AbstractInstance {

    private String dir;

    private String user;

    private String ip;

    private int port;

    private String fingerprintPath;

    @Singular
    private List<String> prepareEnvCommands;

    @Singular
    private List<Path> instanceFiles;

    private Client client;

    @Singular
    private List<EthNode> nodes;

    private List<String> commands;

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EthInstance prepare() {
        addCommands(prepareEnvCommands);
        return this;
    }

    @Override
    public EthInstance run() {
        if (client == null) {
            this.client = new SSHClient(user, fingerprintPath, ip, port);
        }

        client.connect();
        return this;
    }

    @Override
    public EthInstance clean() {
        addCommand("sudo rm -rf " + dir);
        addCommand("sudo docker rm -f $(docker ps -a -q)");
        return this;
    }

    @Override
    public void terminate() {
        client.close();
    }

    @Override
    public EthInstance addCommand(String command) {
        commands.add(command);
        return this;
    }

    @Override
    public EthInstance addCommands(Collection<String> commands) {
        this.commands.addAll(commands);
        return this;
    }

    @Override
    public void exec() {
        commands.forEach(command -> client.exec(command));
        client.reconnect();
    }

    @Override
    public void uploadFile(String source, String target) {
        client.send(source, target);
    }

    @Override
    public InputStream downloadFile(String path) {
        return client.get(path);
    }

}
