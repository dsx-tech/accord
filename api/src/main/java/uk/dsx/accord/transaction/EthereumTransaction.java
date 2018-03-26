package uk.dsx.accord.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EthereumTransaction extends AbstractTransaction {
    String hash;
    String nonce;
    String blockHash;
    String blockNumber;
    String transactionIndex;
    String from;
    String to;
    String value;
    String gas;
    String gasPrice;
    String input;

    @Override
    public String getId() {
        return hash;
    }
}
