package uk.dsx.accord.chain;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import uk.dsx.accord.block.AbstractBlock;
import uk.dsx.accord.peer.AbstractPeerInfo;
import uk.dsx.accord.transaction.AbstractTransaction;

@AllArgsConstructor
@FieldDefaults(makeFinal=true, level= AccessLevel.PROTECTED)
public abstract class BasicChain<TPeerInfo extends AbstractPeerInfo,
        TBlock extends AbstractBlock,
        TTransaction extends AbstractTransaction> {

    String address;

    public abstract Iterable<TPeerInfo> getPeers() throws UnirestException;
    public abstract TBlock getBlock(String hash) throws UnirestException;
    public abstract String sendTransaction(TTransaction transaction) throws UnirestException;
    public abstract int getPeerCount() throws UnirestException;
    public abstract long getBalance(String address) throws UnirestException;
    public abstract int getTransactionCount(String address) throws UnirestException;
}
