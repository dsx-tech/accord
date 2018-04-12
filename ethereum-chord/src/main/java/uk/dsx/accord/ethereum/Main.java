package uk.dsx.accord.ethereum;

import uk.dsx.accord.ethereum.config.DefaultConfiguration;

public class Main {

    public static void main(String[] args) throws InstantiationException, InterruptedException {
        EthInstanceManager manager = new EthInstanceManager();
        manager.withConfig("Absolute path to config here", DefaultConfiguration.class);
        manager.run();
        System.out.println(manager.getRpcIpPorts());
//        manager.terminate();
    }

}
