package uk.dsx.accord.common;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface Instance {

    void start();

    Instance prepare();

    Instance connect();

    Instance clean();

    void terminate();

    void disconnect();

    Instance addCommand(String command);

    Instance addCommands(Collection<String> commands);

    void exec();


    void uploadFiles(List<Path> files);

    void uploadFile(InputStream source, String target);

    InputStream downloadFile(String path);

}
