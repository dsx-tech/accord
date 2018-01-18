package uk.dsx.accord.ethereum;

import uk.dsx.accord.common.InstanceManager;
import uk.dsx.accord.common.config.Configuration;
import uk.dsx.accord.ethereum.config.DefaultConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EthInstanceManager implements InstanceManager<EthInstance> {

    private DefaultEthRunner runner;
    private List<EthInstance> instances;

    public EthInstanceManager() {
        this.runner = new DefaultEthRunner();
        this.instances = new ArrayList<>();
    }

    @Override
    public InstanceManager<EthInstance> withConfig(String config, Class<? extends Configuration> mapped) {
        EthConfigLoader loader = new EthConfigLoader();
        loader.loadConfig(config, (Class<DefaultConfiguration>) mapped);
        return this;
//        throw new UnsupportedOperationException();
    }

    public InstanceManager<EthInstance> addInstance(EthInstance instance) {
        instances.add(instance);
        return this;
    }


    public InstanceManager<EthInstance> addAllInstance(Collection<EthInstance> instance) {
        instances.addAll(instances);
        return this;
    }

    @Override
    public void uploadToAllNodes(String source, String target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void downloadFromNodes(String source, String target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        runner.run(instances);
    }

    public void terminate() {
        instances.forEach(EthInstance::terminate);
    }

    @Override
    public List<EthInstance> getAllInstances() {
        return instances;
    }
}
