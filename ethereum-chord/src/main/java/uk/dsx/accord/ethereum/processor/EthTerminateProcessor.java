package uk.dsx.accord.ethereum.processor;

import uk.dsx.accord.common.InstanceProcessor;
import uk.dsx.accord.ethereum.EthInstance;
import uk.dsx.accord.ethereum.EthInstanceContainer;

public class EthTerminateProcessor implements InstanceProcessor<EthInstanceContainer> {
    @Override
    public void process(EthInstanceContainer container) {
        container.getInstances().forEach(EthInstance::terminate);
    }
}
