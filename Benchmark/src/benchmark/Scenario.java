package benchmark;

import benchmark.core.*;
import benchmark.scenarios.SimpleQueueScenario;
import benchmark.scenarios.SimpleTopicScenario;
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
    protected final String[] args;
    protected final int nodesNo;

    private static final Object commitLock = new Object();

    private final PaxosSTM paxos;
    private final int roundsNo;
    private final boolean isMaster;
    private final String statisticsId = "statistics";
    private final TStatistics statistics;


    public Scenario(int roundsNo, String[] args) {
        this.messagingContext = new BenchmarkMessagingContext();
        this.paxos = PaxosSTM.getInstance();
        this.roundsNo = roundsNo;
        this.args = args;
        this.nodesNo = paxos.getNumberOfNodes();

        initCommitListener();

        isMaster = paxos.getId() == 0;
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

        barrier("scenario-statistics-init");

        statistics = (TStatistics) paxos.getFromSharedObjectRegistry(statisticsId);
    }

    static Class<? extends Scenario> getScenarioClass(AvailableScenarios scenarioType) throws ArgumentParsingException {
        Class<? extends Scenario> scenarioClass;

        switch  (scenarioType) {
            case SimpleQueue:
                scenarioClass = SimpleQueueScenario.class;
                break;
            case SimpleTopic:
                scenarioClass = SimpleTopicScenario.class;
                break;
            default:
                System.err.println("No scenario with such a number");
                throw new ArgumentParsingException();
        }
        return scenarioClass;
    }

    protected abstract RoundStatistics round(int i) throws MessagingException;

    protected long threadsRunner(List<Thread> threads) {
        long start = System.currentTimeMillis();
        try {
            for (Thread t : threads){
                t.start();
            }

            for (Thread t : threads){
                t.join();
            }
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
        for(int i = 0; i < roundsNo; i++) {
            barrier(String.format("start-round-%d", i));
            Logger.log(LogType.Verbose, "########################## Start round %03d ##########################\n", i);

            RoundStatistics roundStatistics = round(i);

            logStats("Round summary", roundStatistics, LogType.Csv);

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
                allRound.csvPrefix = roundStatistics.csvPrefix;
                logStats("Round summary for all nodes", allRound, LogType.CsvMinimal);
            }

            makeSnapshot();
            System.gc();
            System.gc();
            barrier("cleanup");
            makeSnapshot();
            System.gc();
            System.gc();
        }
    }

    private void logStats(String title, RoundStatistics stats, LogType csvType) {
        Logger.log(LogType.Verbose, "%s\n", title);
        Logger.log(LogType.Verbose, "Total round execution time: %d ms\n", stats.executionTime);
        Logger.log(LogType.Verbose, "%s\n", stats.statistics.getStatisticsLog());
        Logger.log(csvType, "%s\n", stats.getCsv());
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
        T get();
    }

    private <T> T getValueOfTObject(TGetter<T> getter) {
        //noinspection unchecked
        final T[] TObj = (T[])new Object[1];
        new Transaction(true) {
            @Override
            public void atomic() {
                TObj[0] = getter.get();
            }
        };

        return TObj[0];
    }

}
