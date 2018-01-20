package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.dsx.accord.common.config.Configuration;

import java.util.List;

@Data
public class DefaultConfiguration implements Configuration {

    @JsonProperty("all-nodes-files")
    private List<String> allNodeFiles;

    @JsonProperty("instances")
    private List<InstanceConfig> instances;

}
