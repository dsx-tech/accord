package uk.dsx.accord.common;

import uk.dsx.config.Configuration;

//That not loader but i dont know how to name it
// May be instance configurator
public interface ConfigurationLoader<Config extends Configuration> {

    void loadConfig(String confFile, Class<Config> configClass);

}
