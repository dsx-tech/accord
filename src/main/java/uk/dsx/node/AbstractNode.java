package uk.dsx.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import uk.dsx.driver.EnvironmentManager;

/**
 * Created by alexander on 22.11.17.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EthNode.class, name = "eth")
})
public abstract class AbstractNode {
    @Setter @Getter
    protected String dockerName;


    /*
     * create files, load its to instance, run dockerfile
     */
    public abstract void startNode(EnvironmentManager client);
}
