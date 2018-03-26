package uk.dsx.accord.block;

import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.dsx.accord.transaction.EthereumTransaction;

@Value
@EqualsAndHashCode(callSuper = true)
public class EthereumBlock extends AbstractBlock {
    String hash;
    String difficulty;
    String extraData;
    String gasLimit;
    String gasUsed;
    String logsBloom;
    String miner;
    String mixHash;
    String nonce;
    String number;
    String parentHash;
    String receiptsRoot;
    String sha3Uncles;
    String size;
    String stateRoot;
    String timestamp;
    String totalDifficulty;
    Iterable<EthereumTransaction> transactions;
    String transactionsRoot;
    String[] uncles;

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public Iterable<EthereumTransaction> getTransactions() {
        return transactions;
    }
}
