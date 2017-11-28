package uk.dsx.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.dsx.driver.EnvironmentManager;

/**
 * Created by alexander on 27.11.17.
 */
public class EthNode extends AbstractNode {

    @JsonCreator
    public EthNode(
            @JsonProperty("dockerName") String dockerName){
        this.dockerName = dockerName;
    }
    @Override
    public void startNode(EnvironmentManager client) {

    }
}
