package uk.dsx.config.ethereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
class InstanceConfig {

    @JsonProperty("name")
    String name;

    @JsonProperty("ip")
    String ip;

    @JsonProperty("port")
    int port;

    @JsonProperty("fingerprint")
    String fingerprintPath;

    @JsonProperty("prepare-env-commands")
    List<String> prepareEnvCommands;

    @JsonProperty("instance-files")
    List<String> instanceFiles;

    @JsonProperty("instance-specified-nodes-files")
    List<String> instanceSpecifiedNodesFiles;

    @JsonProperty("nodes")
    List<NodeConfig> nodes;

}