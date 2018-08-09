package edu.put.paxosstm.messaging.core.utils;

import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.dstm.TransactionStatistics;

public class TransactionStatisticsCollector implements MStatistics {
    public class Statistics {
        public class Stats {
            public int commits;
            public int committedExecTime;
            public int rollbacks;
            public long rolledExecTime;
            public int localAborts;
            public long localAbortedExecTime;
            public int globalAborts;
            public long globalAbortedExecTime;
            public int retries;
            public long retriedExecTime;


            public String getHeader() {
                return "| commit <- time  | rollba <- time  | lAbort <- time  | gAbort <- time  | retry  <- time  ";
            }

            @Override
            public String toString() {
                return String.format(
                        "| %06d < %06d | %06d < %06d | %06d < %06d | %06d < %06d | %06d < %06d ",
                        commits, committedExecTime, rollbacks, rolledExecTime, localAborts, localAbortedExecTime,
                        globalAborts, globalAbortedExecTime, retries, retriedExecTime);
            }
        }

        public Stats readOnly = new Stats();
        public Stats readWrite = new Stats();


        public String getStatisticsLog() {
            return
                " -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "|                                                                                      Statistics                                                                                     |\n" +
                "|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|\n" +
                "|                                         readWrite                                        |                                         readOnly                                         |\n" +
                "|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|\n" +
                String.format("%s %s |\n", readWrite.getHeader(), readOnly.getHeader()) +
                String.format("%s %s |\n", readWrite, readOnly);

        }
    }

    protected abstract class CoreTransaction extends Transaction {
        protected CoreTransaction() {
        }

        protected CoreTransaction(boolean readOnly) {
            super(readOnly);
        }

        @Override
        public final void statistics(TransactionStatistics statistics) {

            if(collectStatistics) {
                collectStatistics(statistics, isReadOnly());
            }
        }

    }

    private final Statistics collectedStatistics = new Statistics();
    public Statistics getCollectedStatistics() {
        return collectedStatistics;
    }

    public boolean collectStatistics = false;

    public void  collectStatistics(TransactionStatistics statistics, boolean isReadOnly) {
        Statistics.Stats stats = isReadOnly ? collectedStatistics.readOnly : collectedStatistics.readWrite;
        switch (statistics.getState()) {
            case Committed:
                stats.commits++;
                stats.committedExecTime += statistics.getExecutionTime();
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
