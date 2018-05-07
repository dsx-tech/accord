package uk.dsx.accord.ethereum.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChainConfig {

    Integer chainId = 497;
    String initialBalance = "1000";
    boolean turnOnEtherbase = true;

    String commonOptions = "--networkid={networkId} --rpc --rpcaddr=0.0.0.0 --rpcapi=db,eth,net,web3,personal --rpccorsdomain=\\'*\\' --nodiscover --verbosity=4";
    String minerOptions = "--mine --minerthreads=1 --etherbase={etherbase}";
    Genesis genesis = new Genesis();


    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Genesis {
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
    public static class Config {
        Integer chainId = 497;
        Long homesteadBlock = 0L;
        Long eip155Block = 0L;
        Long eip158Block = 0L;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @AllArgsConstructor
    public static class Alloc {
        String balance;
    }

}
