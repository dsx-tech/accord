package uk.dsx.accord.ethereum;

import uk.dsx.accord.common.InstanceManager;
import uk.dsx.accord.common.Processor;
import uk.dsx.accord.common.config.Configuration;
import uk.dsx.accord.ethereum.config.DefaultConfiguration;
import uk.dsx.accord.ethereum.processor.EthRunProcessor;
import uk.dsx.accord.ethereum.processor.EthTerminateProcessor;

import java.util.Collection;
import java.util.List;

public class EthInstanceManager implements InstanceManager<EthInstance> {

    //Empty now
    private List<Processor<EthInstanceContainer>> processors;
    private Processor<EthInstanceContainer> runProcessor;
    private Processor<EthInstanceContainer> terminateProcessor;
    private EthInstanceContainer instanceContainer;


    public EthInstanceManager() {
        runProcessor = new EthRunProcessor();
        terminateProcessor = new EthTerminateProcessor();
        instanceContainer = new EthInstanceContainer();
    }

    @Override
    public InstanceManager<EthInstance> withConfig(String config, Class<? extends Configuration> mapped) {
        EthConfigLoader loader = new EthConfigLoader();
        instanceContainer.apply(loader.loadConfig(config, (Class<DefaultConfiguration>) mapped));
        return this;
    }

    public InstanceManager<EthInstance> addInstance(EthInstance instance) {
        instanceContainer.getInstances().add(instance);
        return this;
    }


    public InstanceManager<EthInstance> addAllInstances(Collection<EthInstance> instance) {
        instanceContainer.getInstances().addAll(instance);
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
//        processors.forEach(processor -> processor.process());
        runProcessor.process(instanceContainer);
    }

    public void terminate() {
        terminateProcessor.process(instanceContainer);
    }

    @Override
    public List<EthInstance> getAllInstances() {
        return instanceContainer.getInstances();
    }
}
