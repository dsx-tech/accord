package uk.dsx.node;

import lombok.Setter;

import java.util.List;

/**
 * Created by alexander on 22.11.17.
 */
public abstract class AbstractNode {
    @Setter
    protected String user;
    @Setter
    protected String ip;
    @Setter
    protected String dockerName;

    public abstract void prepareEnvironment(List<String> sharedFiles);

    public abstract void startNode();

    @Override
    public String toString() {
        return "user='" + user + '\'' +
               ", ip='" + ip + '\'' +
               ", dockerName='" + dockerName + '\'';
    }
}
