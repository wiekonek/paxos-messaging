package edu.put.paxosstm.messaging.core.utils;

import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.dstm.TransactionStatistics;

public class TransactionStatisticsCollector implements MStatistics {

    protected abstract class CoreTransaction extends Transaction {
        protected CoreTransaction() {
        }

        protected CoreTransaction(boolean readOnly) {
            super(readOnly);
        }

        @Override
        public final void statistics(TransactionStatistics statistics) {

            if(collectStatistics) {
                synchronized(this) {
                    collectStatistics(statistics, isReadOnly());
                }
            }
        }

    }

    private final Statistics collectedStatistics = new Statistics();

    @Override
    public synchronized Statistics getCollectedStatistics() {
        return collectedStatistics;
    }

    public boolean collectStatistics = false;

    private void  collectStatistics(TransactionStatistics statistics, boolean isReadOnly) {
        Statistics.Stats stats = isReadOnly ? collectedStatistics.readOnly : collectedStatistics.readWrite;
        switch (statistics.getState()) {
            case Committed:
                stats.commits++;
                stats.committedExecTime += statistics.getExecutionTime();
                collectedStatistics.committedPSize.packageSize += statistics.getPackageSize();
                collectedStatistics.committedPSize.readSetSize += statistics.getReadSetSize();
                collectedStatistics.committedPSize.writeSetSize += statistics.getWriteSetSize();
                collectedStatistics.committedPSize.newSetSize += statistics.getNewSetSize();
                collectedStatistics.committedPSize.typeSetSize += statistics.getTypeSetSize();
                break;
            case RolledBack:
                stats.rollbacks++;
                stats.rolledExecTime += statistics.getExecutionTime();
                break;
            case GlobalAbort:
                stats.globalAborts++;
                stats.globalAbortedExecTime += statistics.getExecutionTime();
                collectedStatistics.globalAbortedPSize.packageSize += statistics.getPackageSize();
                collectedStatistics.globalAbortedPSize.readSetSize += statistics.getReadSetSize();
                collectedStatistics.globalAbortedPSize.writeSetSize += statistics.getWriteSetSize();
                collectedStatistics.globalAbortedPSize.newSetSize += statistics.getNewSetSize();
                collectedStatistics.globalAbortedPSize.typeSetSize += statistics.getTypeSetSize();
                break;
            case LocalAbort:
                stats.localAborts++;
                stats.localAbortedExecTime += statistics.getExecutionTime();
                break;
            case Retried:
                stats.retries++;
                stats.retriedExecTime += statistics.getExecutionTime();
                break;
        }
    }


}
