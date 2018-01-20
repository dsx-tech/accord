package uk.dsx.accord.ethereum;

import uk.dsx.accord.common.InstanceProcessor;

public class EthTerminateProcessor implements InstanceProcessor<EthInstanceContainer> {
    @Override
    public void process(EthInstanceContainer container) {
        container.getInstances().forEach(EthInstance::terminate);
    }
}
