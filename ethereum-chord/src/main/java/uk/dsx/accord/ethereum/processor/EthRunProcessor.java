package uk.dsx.accord.ethereum.processor;

import uk.dsx.accord.common.InstanceProcessor;
import uk.dsx.accord.ethereum.EthInstance;
import uk.dsx.accord.ethereum.EthInstanceContainer;
import uk.dsx.accord.ethereum.EthNode;

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
        String bootEnode = bootInstance.getEnode("boot");

        // Must be EthNode.run
        for (EthInstance instance : instances) {
            for (int port = 30304; port < 30305; port++) {
                instance.runNode("node" + port, bootEnode, port);
            }
        }
    }

    public void process2(EthInstanceContainer container) {
        boolean booted = false;
        List<EthInstance> instances = container.getInstances();
        EthInstance bootInstance = instances.stream().findFirst().orElseThrow(() -> new RuntimeException("No instances"));
        bootInstance.run()
                .prepare()
                .clean()
                .exec();
//        EthNode bootNode = bootInstance.getNodes().stream().findFirst().orElseThrow(() -> new RuntimeException("No nodes"));
//        bootInstance.runNode(bootNode.getName(), "boot", bootNode.getPort());
        //Start bootNode
        bootInstance.runNode("boot", "boot", 30303);
        String bootEnode = bootInstance.getEnode("boot");

        // Must be EthNode.run
        for (EthInstance instance : instances) {
            for (EthNode node : instance.getNodes()) {
                instance.runNode(node.getName(), bootEnode, node.getPort());
            }
        }
    }

}
