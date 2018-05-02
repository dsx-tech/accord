package uk.dsx.accord.ethereum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import org.apache.commons.io.FilenameUtils;
import uk.dsx.accord.common.AbstractInstance;
import uk.dsx.accord.common.Client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    @Singular
    private List<Path> postInitFiles;

    @Singular
    private List<String> postInitCommands;

    private Client client;

    @Singular
    private List<EthCommonNode> nodes;

    private List<String> commands;

    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EthInstance connect() {
        return this;
    }

    @Override
    public EthInstance prepare() {
        addCommands(prepareEnvCommands);
        addCommand("mkdir -p " + dir);
        return this;
    }

    @Override
    public EthInstance clean() {
        addCommand("sudo docker stop $(docker ps -q -f name=ethereum)");
        addCommand("sudo docker rm $(docker ps -aq -f name=ethereum)");
        addCommand("sudo rm -rf " + dir);
        return this;
    }

    @Override
    public void disconnect() {
        client.close();
    }

    @Override
    public void terminate() {
        throw new UnsupportedOperationException();
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
        commands.clear();
    }

    public void exec(String command) {
        client.exec(command);
    }

    @Override
    public void uploadFile(InputStream source, String target) {
        client.sendFile(source, target);
    }

    @Override
    public void uploadFile(String source, String target) {
        client.sendFile(source, target);
    }


    @Override
    public void uploadFile(URL source) {
        try {
            String name = FilenameUtils.getName(source.getPath());
            InputStream fileStream = source.openStream();
            client.sendFile(fileStream, dir + "/" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFiles(List<Path> files) {
        files.forEach(filePath -> uploadFile(filePath.toString(), dir + "/" + filePath.getFileName().toString()));
    }

    @Override
    public InputStream downloadFile(String path) {
        return client.getFile(path);
    }

}
