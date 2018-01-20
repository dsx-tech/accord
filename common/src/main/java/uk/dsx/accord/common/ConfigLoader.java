package uk.dsx.accord.common;

import uk.dsx.accord.common.config.Configuration;

public interface ConfigLoader<Container extends InstanceContainer, Config extends Configuration> {

    Container loadConfig(String file, Class<? extends Config> configClass);

}
