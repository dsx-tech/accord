package uk.dsx.accord.ethereum.processor;

import uk.dsx.accord.common.InstanceProcessor;
import uk.dsx.accord.ethereum.EthCommonNode;
import uk.dsx.accord.ethereum.EthInstance;
import uk.dsx.accord.ethereum.EthInstanceContainer;
import uk.dsx.accord.ethereum.PortBinding;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EthRunProcessor implements InstanceProcessor<EthInstanceContainer> {

    @Override
    public void process(EthInstanceContainer container) {
        List<EthInstance> instances = container.getInstances();

        //Prepare instance and folder
        instances.forEach(instance -> instance.connect().clean().prepare().exec());

        // Upload accord-sh api
        instances.forEach(instance -> instance.uploadFile(getClass().getResource("/ethereum/accord")));

        //Wait
        waitBefore(2000);

        instances.forEach(instance -> instance.exec("chmod +x " + instance.getDir() + "/accord"));


        //Wait
        waitBefore(2000);

        // Upload Instance files
        instances.forEach(instance -> instance.uploadFiles(instance.getInstanceFiles()));

        // Collect all nodes. Move this to container.
        List<EthCommonNode> nodes = instances.stream()
                .map(EthInstance::getNodes)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        //Run all nodes
        nodes.forEach(EthCommonNode::run);

        //Wait
        waitBefore(2000);

        //Map enodes
        Map<EthCommonNode, String> enodes = nodes.stream().collect(Collectors.toMap(node -> node, EthCommonNode::requestEnode));
        Map<EthCommonNode, List<PortBinding>> ports = nodes.stream().collect(Collectors.toMap(node -> node, EthCommonNode::requestPorts));
        Map<String, String> transformedEnodes = nodes.stream().collect(Collectors.toMap(EthCommonNode::getName, node -> {
            String ip = node.getIp();
            String enode = enodes.get(node);
            PortBinding port = getP2PPort(ports.get(node));
            return transformEnode(enode, ip, port.getHostPort(), port.getExposedPort());
        }));

        //Wait
        waitBefore(3000);

//         Add peers.
        nodes.forEach(node -> node.getNodePeers().forEach(peer -> {
            waitBefore(1000);
            node.addPeer(transformedEnodes.getOrDefault(peer, ""));
        }));

        // #######################
        // Post chain init
        // ######################
        waitBefore(2000);
        instances.forEach(instance -> instance.uploadFiles(instance.getPostInitFiles()));
        waitBefore(2000);
        instances.forEach(instance -> instance.addCommands(instance.getPostInitCommands()).exec());

    }

    private PortBinding getP2PPort(List<PortBinding> ports) {
        return ports.stream()
                .filter(portBinding -> portBinding.getProtocol().equals("tcp"))
                .filter(portBinding -> portBinding.getExposedPort().equals("30303"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Port not found"));
    }

    private String transformEnode(String enode, String ip, String port, String replacedPort) {
        return enode
                .replace("[::]", ip)
                .replace(":" + replacedPort, ":" + port);
    }

    public void waitBefore(int timeout) {
        //Wait
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
