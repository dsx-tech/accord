package uk.dsx.accord.ethereum;

import uk.dsx.accord.common.InstanceProcessor;

import java.util.List;

public class EthRunProcessor implements InstanceProcessor<EthInstanceContainer> {

    @Override
    public void process(EthInstanceContainer container) {
        List<EthInstance> instances = container.getInstances();
        EthInstance bootInstance = instances.stream().findFirst().orElseThrow(() -> new RuntimeException("No instances"));
        bootInstance.run()
                .prepare()
                .clean()
                .exec();

        //Start bootNode
        bootInstance.runNode("boot", "boot", 30303);
        String boot_enode = bootInstance.getEnode("boot");

        // Must be EthNode.run
        for (EthInstance instance : instances) {
            for (int port = 30304; port < 30305; port++) {
                instance.runNode("node" + port, boot_enode, port);
            }
        }
    }

}
