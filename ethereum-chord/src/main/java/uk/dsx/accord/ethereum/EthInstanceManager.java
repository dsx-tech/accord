package uk.dsx.accord.ethereum;

import uk.dsx.Instance;
import uk.dsx.InstanceManager;
import uk.dsx.config.Configuration;
import uk.dsx.ethereum.config.DefaultConfiguration;

import java.util.List;

public class EthInstanceManager implements InstanceManager<EthInstance> {

    EthConfigLoader configLoader = new EthConfigLoader();


    // SOLVE GENERICS PROBLEM. Normal parametrization should help
    @Override
    public InstanceManager<EthInstance> withConfig(String config, Class<? extends Configuration> mapped) {
        if (mapped.isAssignableFrom(DefaultConfiguration.class)) {
            Class<DefaultConfiguration> mappedConfig = (Class<DefaultConfiguration>) mapped;
            configLoader.loadConfig(config, mappedConfig);
        }
        // if (mapped.isAssignableFrom(AnotherConfig.class)
        // do same with another loader
        return this;
    }

    @Override
    public void uploadToAllNodes(String source, String target) {

    }

    @Override
    public void downloadFromNodes(String source, String target) {

    }

    @Override
    public void runAllNodes() {

    }

    @Override
    public List<Instance> getAllInstances() {
        return null;
    }
}
