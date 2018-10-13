package edu.put.paxosstm.messaging.core.utils;

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

    public class PackageSize implements Serializable {
        public int packageSize;
        public int readSetSize;
        public int writeSetSize;
        public int newSetSize;
        public int typeSetSize;

        PackageSize add(PackageSize b) {
            PackageSize result = new PackageSize();
            result.packageSize = packageSize + b.packageSize;
            result.readSetSize = readSetSize + b.readSetSize;
            result.writeSetSize = writeSetSize + b.writeSetSize;
            result.newSetSize = newSetSize + b.newSetSize;
            result.typeSetSize = typeSetSize + b.typeSetSize;
            return result;
        }

        String toCsv() {
            return Stream
                    .of(packageSize, readSetSize, writeSetSize, newSetSize, typeSetSize)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }

        String getHeader() {
            return "| pkgSiz |  rSet  |  wSet  | newSet | typSet ";
        }

        @Override
        public String toString() {
            return String.format(
                    "| %06d | %06d | %06d | %06d | %06d ",
                    packageSize, readSetSize, writeSetSize, newSetSize, typeSetSize);
        }
    }

    public Stats readOnly = new Stats();
    public Stats readWrite = new Stats();
    public PackageSize committedPSize = new PackageSize();
    public PackageSize globalAbortedPSize = new PackageSize();

    public String toCsv() {
        return readWrite.toCsv() + "," + readOnly.toCsv() + "," +
                committedPSize.toCsv() + "," + globalAbortedPSize.toCsv();
    }
    
    public Statistics add(Statistics statistics) {
        Statistics result = new Statistics();
        result.readWrite = readWrite.add(statistics.readWrite);
        result.readOnly = readOnly.add(statistics.readOnly);
        result.committedPSize = committedPSize.add(statistics.committedPSize);
        result.globalAbortedPSize = globalAbortedPSize.add(statistics.globalAbortedPSize);
        return result;
    }

    public String getStatisticsLog() {
        return
                " -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "|                                                                               Transaction statistics                                                                              |                                      Messages size                                      |\n" +
                "|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|\n" +
                "|                                         readWrite                                       |                                         readOnly                                        |                  Commited                  |                Globaly aborted             |\n" +
                "|-----------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|--------------------------------------------|--------------------------------------------|\n" +
                String.format("%s%s%s%s|\n", readWrite.getHeader(), readOnly.getHeader(), committedPSize.getHeader(), globalAbortedPSize.getHeader()) +
                String.format("%s%s%s%s|\n", readWrite, readOnly, committedPSize, globalAbortedPSize);

    }
}