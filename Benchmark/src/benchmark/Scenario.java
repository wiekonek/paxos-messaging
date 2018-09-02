package benchmark;

import benchmark.core.*;
import benchmark.scenarios.ProdConsScenario;
import benchmark.scenarios.ProdConsTopicScenario;
import benchmark.scenarios.workers.PaxosWorker;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import soa.paxosstm.common.CheckpointListener;
import soa.paxosstm.common.Storage;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class Scenario {

    protected final BenchmarkMessagingContext messagingContext;
    protected final PaxosSTM paxos;
    protected final int nodeId;
    protected final String[] args;

    private static final Object commitLock = new Object();

    private final int roundsNo;
    private final boolean isMaster;
    private final String statisticsId = "statistics";
    private final TStatistics statistics;


    public Scenario(int roundsNo, String[] args) {
        this.messagingContext = new BenchmarkMessagingContext();
        this.paxos = PaxosSTM.getInstance();
        this.nodeId = paxos.getId();
        this.roundsNo = roundsNo;
        this.args = args;

        isMaster = nodeId == 0;

        if(isMaster) {
            new Transaction() {
                @Override
                public void atomic() {
                    if (paxos.getFromSharedObjectRegistry(statisticsId) == null) {
                        TStatistics s = new TStatistics();
                        paxos.addToSharedObjectRegistry(statisticsId, s);
                    }
                }
            };
        }

        initCommitListener();
        barrier("scenario-statistics-init");

        statistics = (TStatistics) PaxosSTM.getInstance().getFromSharedObjectRegistry(statisticsId);
    }

    static Class<? extends Scenario> getScenarioClass(AvailableScenarios scenarioType) throws ArgumentParsingException {
        Class<? extends Scenario> scenarioClass;

        switch  (scenarioType) {
            case SimpleQueue:
                scenarioClass = ProdConsScenario.class;
                break;
            case SimpleTopic:
                scenarioClass = ProdConsTopicScenario.class;
                break;
            default:
                System.err.println("No scenario with such a number");
                throw new ArgumentParsingException();
        }
        return scenarioClass;
    }

    protected abstract RoundStatistics round() throws MessagingException;

    protected long threadsRunner(List<Thread> threads) {
        long start = System.currentTimeMillis();
        try {
            for (Thread t : threads) t.start();
            for (Thread t : threads) t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - start;
    }

    protected LinkedHashMap<String, Long> collectWorkersExecutionTimes(List<PaxosWorker> workers) {
        LinkedHashMap<String, Long> threadExecutionTimes = new LinkedHashMap<>();
        for (PaxosWorker worker : workers) {
            threadExecutionTimes.put(worker.getFullName(), worker.getExecutionTime());
        }
        return threadExecutionTimes;
    }

    protected void barrier(String barrierName) {
        paxos.enterBarrier(barrierName, paxos.getNumberOfNodes());
    }

    void run() throws MessagingException {
        barrier("start-scenario");
        RoundStatistics nodeStatistics = new RoundStatistics();
        for(int i = 0; i < roundsNo; i++) {
            barrier(String.format("start-round-%d", i));
            Logger.log("########################## Start round %03d ##########################\n", i);

            RoundStatistics roundStatistics = round();

            logStats("Round summary", roundStatistics);

            new Transaction() {
                @Override
                public void atomic() {
                    statistics.add(roundStatistics);
                }
            };
            barrier(String.format("end-round-%d", i));
            if (isMaster) {
                RoundStatistics allRound = getValueOfTObject(statistics::get);
                new Transaction() {
                    @Override
                    public void atomic() {
                        statistics.clear();
                    }
                };
                allRound.threadExecutionTimes.clear();
                logStats("Round summary for all nodes", allRound);
            }

            makeSnapshot();
            nodeStatistics = nodeStatistics.add(roundStatistics);
        }

        Logger.log("########################## Global summary ##########################");
        logStats("Node summary", nodeStatistics);

        RoundStatistics finalNodeStatistics = nodeStatistics;
        new Transaction() {
            @Override
            public void atomic() {
                statistics.add(finalNodeStatistics);
            }
        };
        barrier("collect-scenario-stats");


        RoundStatistics roundStatistics = getValueOfTObject(statistics::get);
        roundStatistics.threadExecutionTimes.clear();
        Logger.log("Alle nodes summary");
        Logger.log("%s\n", roundStatistics.statistics.getStatisticsLog());
        Logger.logCsv("[%s],%s\n\n", String.join(" ", args), roundStatistics.getCsv());
    }

    private void logStats(String title, RoundStatistics stats) {
        Logger.log("%s\n", title);
        Logger.log("Total round execution time: %d ms\n", stats.executionTime);
        Logger.log("%s\n", stats.statistics.getStatisticsLog());
        Logger.logCsv("%s\n\n", stats.getCsv());
    }

    private static void initCommitListener() {
        PaxosSTM.getInstance().addCheckpointListener(new CheckpointListener() {
            @Override
            public void onCheckpoint(int seqNumber, Storage storage) {
            }

            @Override
            public void onCheckpointFinished(int seqNumber) {
                synchronized (commitLock) {
                    commitLock.notifyAll();
                }
            }
        });
    }

    private static void makeSnapshot() {
        synchronized (commitLock) {
            PaxosSTM.getInstance().scheduleCheckpoint();
            try {
                commitLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    interface TGetter<T> {
        T select();
    }

    private <T> T getValueOfTObject(TGetter<T> selector) {
        //noinspection unchecked
        final T[] TObj = (T[])new Object[1];
        new Transaction(true) {
            @Override
            public void atomic() {
                TObj[0] = selector.select();
            }
        };

        return TObj[0];
    }

}
