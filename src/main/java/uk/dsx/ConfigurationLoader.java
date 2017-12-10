package uk.dsx;

import uk.dsx.config.Configuration;

import java.util.List;

//That not loader but i dont know how to name it
// May be instance configurator
public interface ConfigurationLoader<Config extends Configuration> {

    void loadConfig(String confFile, Class<Config> configClass);

    void setupInstances(List<Instance> instances);

    void setupNodes(List<Node> nodes);

}
