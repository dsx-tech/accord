package uk.dsx.config.ethereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.dsx.config.Configuration;

import java.util.List;

@Data
public class DefaultConfiguration extends Configuration {

    @JsonProperty("all-nodes-files")
    List<String> allNodeFiles;

    @JsonProperty("instances")
    List<InstanceConfig> instances;

}
