package uk.dsx.accord.common.client;

import uk.dsx.Client;

public class LocalhostClient implements Client {
    @Override
    public <C extends Client> C connect() throws InstantiationException {
        return null;
    }

    @Override
    public <C extends Client> C send(String sourcePath, String targetPath) {
        return null;
    }

    @Override
    public <C extends Client> C get(String sourcePath, String targetPath) {
        return null;
    }

    @Override
    public <C extends Client> C exec(String command) {
        return null;
    }

    @Override
    public <C extends Client> C close() {
        return null;
    }
}
