package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeType {

    @JsonProperty("miner")
    MINER("miner"),

    @JsonProperty("observer")
    OBSERVER("observer");

    final String simpleName;


    @Override
    public String toString() {
        return simpleName;
    }
}
