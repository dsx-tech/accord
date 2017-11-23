package uk.dsx.driver;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alexander on 22.11.17.
 */
public abstract class EnvironmentManager implements Closeable {
    public abstract boolean executeCommands(List<String> commands, String userName, String ip);
    public abstract boolean uploadFiles(List<Path> files, String user, String ip);
}
