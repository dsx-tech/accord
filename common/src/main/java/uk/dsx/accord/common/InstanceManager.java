package uk.dsx.accord.common;

import uk.dsx.accord.common.config.Configuration;

import java.util.List;

public interface InstanceManager<T extends Instance> {

    InstanceManager<T> withConfig(String config, Class<? extends Configuration> mapped);

    void uploadToAllNodes(String source, String target);

    void downloadFromNodes(String source, String target);

    void run();

    List<? extends Instance> getAllInstances();

}
