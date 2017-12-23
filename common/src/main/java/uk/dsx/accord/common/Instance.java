package uk.dsx.accord.common;

import java.io.InputStream;
import java.util.Collection;

public interface Instance {

    void start();

    Instance prepare();

    Instance run();

    Instance clean();

    void terminate();

    Instance addCommand(String command);

    Instance addCommands(Collection<String> commands);

    void exec();

    void uploadFile(String source, String target);

    InputStream downloadFile(String path);

}