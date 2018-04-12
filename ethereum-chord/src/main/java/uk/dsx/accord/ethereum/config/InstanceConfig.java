package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class InstanceConfig {

    @JsonProperty("name")
    private String name;

    @JsonProperty("user")
    private String user;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("port")
    private int port;

    @JsonProperty("fingerprint")
    private String fingerprintPath;

    @JsonProperty("working-dir")
    private String workingDir = "./shared_dir";

    @JsonProperty("prepare-env-commands")
    private List<String> prepareEnvCommands;

    @JsonProperty("instance-files")
    private List<String> instanceFiles;

    @JsonProperty("instance-specified-nodes-files")
    private List<String> instanceSpecifiedNodesFiles;

    @JsonProperty("post-init-commands")
    private List<String> postInitCommands = Collections.emptyList();

    @JsonProperty("post-init-files")
    private List<String> postInitFiles = Collections.emptyList();

    @JsonProperty("nodes")
    private List<NodeConfig> nodes;

}
