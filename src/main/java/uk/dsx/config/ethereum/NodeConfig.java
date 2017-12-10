package uk.dsx.config.ethereum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
class NodeConfig {

    @JsonProperty("name")
    String name;

    @JsonProperty("type")
    NodeType type;

    @JsonProperty("node-files")
    List<String> nodeFiles;

    @JsonProperty("params")
    Map<String, String> params;
}
