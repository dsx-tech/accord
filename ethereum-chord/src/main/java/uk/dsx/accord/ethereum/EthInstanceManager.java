package uk.dsx.accord.ethereum;

import uk.dsx.accord.common.Instance;
import uk.dsx.accord.common.InstanceManager;
import uk.dsx.accord.common.config.Configuration;

import java.util.List;

public class EthInstanceManager implements InstanceManager<EthInstance> {


    @Override
    public InstanceManager<EthInstance> withConfig(String config, Class<? extends Configuration> mapped) {
        return null;
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
