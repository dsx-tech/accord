package uk.dsx.accord.common;

import java.io.InputStream;

public interface Client<C extends Client> {

    C sendFile(String sourcePath, String targetPath);

    C getFile(String sourcePath, String targetPath);

    InputStream getFile(String targetPath);

    InputStream exec(String command);

    C close();

}
