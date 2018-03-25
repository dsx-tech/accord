package uk.dsx.accord.chain.ethereum;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Value;
import uk.dsx.accord.block.EthereumBlock;
import uk.dsx.accord.chain.BasicChain;
import uk.dsx.accord.peer.EthereumPeerInfo;
import uk.dsx.accord.transaction.EthereumTransaction;
import uk.dsx.accord.util.HttpHelper;

import java.util.List;

import static java.util.Arrays.asList;
import static uk.dsx.accord.util.HttpHelper.EMPTY_PARAMETERS;

@Value
public class EthereumChain extends BasicChain<EthereumPeerInfo,EthereumBlock,EthereumTransaction> {

    public EthereumChain(String address) {
        super(address);
    }

    @Override
    public Iterable<EthereumPeerInfo> getPeers() throws UnirestException {
        return HttpHelper.<Iterable<EthereumPeerInfo>, List>post(address, "admin_peers", EMPTY_PARAMETERS);

    }

    @Override
    public EthereumBlock getBlock(String hash) throws UnirestException {
        return HttpHelper.<EthereumBlock, List>post(address, "eth_getBlockByNumber", asList(hash, "pending", true));
    }

    public String sendTransaction(EthereumTransaction transaction) throws UnirestException {
        return HttpHelper.<String, List>post(address, "eth_sendTransaction", asList(transaction));
    }

    public int getPeerCount() throws UnirestException {
        String result = HttpHelper.<String, List>post(address, "net_peerCount", EMPTY_PARAMETERS);
        return Integer.parseInt(result.substring(2));
    }

    public long getBalance(String address ) throws UnirestException {
        String weiBalance = HttpHelper.<String, List>post(address, "eth_getBalance", asList(address, "pending"));
        return Long.parseLong(weiBalance.substring(2, weiBalance.length()), 16);
    }

    public int getTransactionCount(String address) throws UnirestException {
        String result = HttpHelper.<String, List>post(address, "eth_getTransactionCount", asList(address, "pending"));
        return Integer.parseInt(result.substring(2));
    }

    public EthereumBlock getBlock(long blockNumber) throws UnirestException {
        return HttpHelper.<EthereumBlock, List>post(address, "eth_getBlockByNumber", asList("0x" + Long.toHexString(blockNumber)));
    }

    public long getLastLastBlockNumber() throws UnirestException {
        String result = HttpHelper.<String, List>post(address, "eth_blockNumber", EMPTY_PARAMETERS);
        return Long.parseLong(result.substring(2), 16);
    }

    public boolean isNetListening() throws UnirestException {
        return HttpHelper.<Boolean, List>post(address, "net_listening", EMPTY_PARAMETERS);
    }

    public int getProtocolVersion() throws UnirestException {
        return HttpHelper.<Integer, List>post(address, "net_listening", EMPTY_PARAMETERS);
    }

    public String getCoinbase() throws UnirestException {
        return HttpHelper.<String, List>post(address, "eth_coinbase", EMPTY_PARAMETERS);
    }

    public boolean isMining() throws UnirestException {
        return HttpHelper.<Boolean, List>post(address, "eth_mining", EMPTY_PARAMETERS);
    }

    public int getHashRate() throws UnirestException {
        return HttpHelper.<Integer, List>post(address, "eth_hashrate", EMPTY_PARAMETERS);
    }

    public long getGasPrice() throws UnirestException {
        String result = HttpHelper.<String, List>post(address, "eth_gasPrice", EMPTY_PARAMETERS);
        return Long.parseLong(result.substring(2), 16);
    }

    public List<String> getAccounts() throws UnirestException {
        return HttpHelper.<List<String>, List>post(address, "eth_accounts", EMPTY_PARAMETERS);
    }
}