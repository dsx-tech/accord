package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;


@Data
public class NodeConfig {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private NodeType type = NodeType.OBSERVER;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("rpc-port")
    private Integer rpcPort;

    @JsonProperty("internalP2PPort")
    private Integer internalP2PPort = 30303;

    @JsonProperty("internalRpcPort")
    private Integer internalRpcPort = 8545;

    @JsonProperty("node-files")
    private List<String> nodeFiles;

    @JsonProperty("peers")
    private List<String> peers = Collections.emptyList();

}
