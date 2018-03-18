package uk.dsx.accord.block;

import uk.dsx.accord.transaction.AbstractTransaction;

public abstract class AbstractBlock {

    public abstract Iterable<? extends AbstractTransaction> getTransactions();
    public abstract String getHash();
}
