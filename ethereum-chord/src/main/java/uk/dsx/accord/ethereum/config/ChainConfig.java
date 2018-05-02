package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChainConfig {

    Integer chainId = 497;
    String initialBalance = "1000";

    @JsonIgnore
    String networkIdOption = !isNull(chainId) ? "--networkid " + chainId : "";
    String commonOptions = networkIdOption + " " + "--rpc --rpcaddr=0.0.0.0 --rpcapi=db,eth,net,web3,personal --rpccorsdomain \"*\" --nodiscover --verbosity=4";
    String minerOptions = "--mine --minerthreads=1";
    Genesis genesis = new Genesis();


    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    class Genesis {
        Config config = new Config();
        String nonce;
        String timestamp;
        String parentHash;
        String gasLimit = "0x2fefd8";
        String difficulty = "0x100";
        String mixHash;
        String coinbase;

        @JsonInclude(JsonInclude.Include.ALWAYS)
        Map<String, Alloc> alloc = new HashMap<>();
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    class Config {

        // Best solution ever
        Integer chainId = ChainConfig.this.chainId;
        Long homesteadBlock = 0L;
        Long eip155Block = 0L;
        Long eip158Block = 0L;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @AllArgsConstructor
    static class Alloc {
        String balance;
    }

}
