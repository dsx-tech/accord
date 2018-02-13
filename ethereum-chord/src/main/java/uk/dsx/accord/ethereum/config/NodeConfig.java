package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class NodeConfig {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private NodeType type;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("node-files")
    private List<String> nodeFiles;

}
