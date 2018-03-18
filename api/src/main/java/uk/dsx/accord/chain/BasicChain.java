package uk.dsx.accord.chain;

import com.mashape.unirest.http.exceptions.UnirestException;
import uk.dsx.accord.block.AbstractBlock;
import uk.dsx.accord.peer.AbstractPeerInfo;
import uk.dsx.accord.transaction.AbstractTransaction;

public interface BasicChain<TPeerInfo extends AbstractPeerInfo,
        TBlock extends AbstractBlock,
        TTransaction extends AbstractTransaction> {
    Iterable<TPeerInfo> getPeers() throws UnirestException;
    TBlock getBlock(String hash) throws UnirestException;
    String sendTransaction(TTransaction transaction) throws UnirestException;
    int getPeerCount() throws UnirestException;
    double getBalance(String address) throws UnirestException;
    int getTransactionCount(String address) throws UnirestException;
}
