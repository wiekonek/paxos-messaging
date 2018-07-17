package edu.put.paxosstm.messaging.core.transactional;

import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.dstm.TransactionStatistics;

public abstract class AsyncTransaction extends Transaction {
    public abstract void atomic();

    @Override
    public void statistics(TransactionStatistics statistics) {
        super.statistics(statistics);
        if(statistics.getState() == TransactionStatistics.State.Committed) {
            asyncAfterCommit();
        }
    }

    public void asyncAfterCommit() {

    }
}
