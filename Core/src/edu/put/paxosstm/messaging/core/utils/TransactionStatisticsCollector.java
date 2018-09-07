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
                stats.committedExecTime += statistics.getExecutionTime(); //mili lub micro sekundy
                break;
            case RolledBack:
                stats.rollbacks++;
                stats.rolledExecTime += statistics.getExecutionTime();
                break;
            case GlobalAbort:
                stats.globalAborts++;
                stats.globalAbortedExecTime += statistics.getExecutionTime();
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
