package edu.put.paxosstm.messaging.core.utils;

import com.sun.org.glassfish.external.statistics.impl.StatisticImpl;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Statistics implements Serializable {
    public class Stats implements Serializable {
        public long commits;
        public long committedExecTime;
        public long rollbacks;
        public long rolledExecTime;
        public long localAborts;
        public long localAbortedExecTime;
        public long globalAborts;
        public long globalAbortedExecTime;
        public long retries;
        public long retriedExecTime;


        String getHeader() {
            return "| commit <- time  | rollba <- time  | lAbort <- time  | gAbort <- time  | retry  <- time  ";
        }
        
        String toCsv() {
            return Stream
                    .of(
                            commits,
                            committedExecTime,
                            rollbacks,
                            rolledExecTime,
                            localAborts,
                            localAbortedExecTime,
                            globalAborts,
                            globalAbortedExecTime,
                            retries,
                            retriedExecTime
                    )
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }
        
        Stats add(Stats stats) {
            Stats result = new Stats();
            result.commits = commits + stats.commits;
            result.committedExecTime = committedExecTime + stats.committedExecTime;
            result.rollbacks = rollbacks + stats.rollbacks;
            result.rolledExecTime = rolledExecTime + stats.rolledExecTime;
            result.localAborts = localAborts + stats.localAborts;
            result.localAbortedExecTime = localAbortedExecTime + stats.localAbortedExecTime;
            result.globalAborts = globalAborts + stats.globalAborts;
            result.globalAbortedExecTime = globalAbortedExecTime + stats.globalAbortedExecTime;
            result.retries = retries + stats.retries;
            result.retriedExecTime = retriedExecTime + stats.retriedExecTime;
            return result;
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

    public String toCsv() {
        return readWrite.toCsv() + "," + readOnly.toCsv();
    }
    
    public Statistics add(Statistics statistics) {
        Statistics result = new Statistics();
        result.readWrite = readWrite.add(statistics.readWrite);
        result.readOnly = readOnly.add(statistics.readOnly);
        return result;
    }

    public String getStatisticsLog() {
        return
                " -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                        "|                                                                                      Statistics                                                                                     |\n" +
                        "|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|\n" +
                        "|                                         readWrite                                        |                                         readOnly                                         |\n" +
                        "|------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|\n" +
                        String.format("%s %s |\n", readWrite.getHeader(), readOnly.getHeader()) +
                        String.format("%s %s |", readWrite, readOnly);

    }
}