package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.benchmark.RoundStatistics;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.benchmark.core.LogType;
import edu.put.paxosstm.messaging.benchmark.core.Logger;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.EmptyTwoQueueConsumer;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.PaxosWorker;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.SimpleQueueProducer;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;
import edu.put.paxosstm.messaging.core.utils.Statistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class QueueWithTransactionScenario extends SimpleQueueScenario {


    public QueueWithTransactionScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
    }

    protected RoundStatistics round(int round) throws MessagingException {
        MQueueParams queueParams = new MQueueParams(
                MQueueType.Multi,
                argsParser.getConcurrentQueueNo(),
                argsParser.getSelectionStrategy(),
                argsParser.getRetryNumber(),
                argsParser.getRetryDelay(),
                argsParser.getMsgListType()
        );
        Logger.log(LogType.Verbose, "Round queues intializing\n");
        MessageQueue q1 = messagingContext.createQueueWithStatisticsCollection("messages-queue-1", queueParams);
        MessageQueue q2 = messagingContext.createQueueWithStatisticsCollection("messages-queue-2", queueParams);
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<PaxosWorker> workers = new ArrayList<>();
        barrier(String.format("init-round-%d", round));

        int producersNo = argsParser.getProducersNo();
        for (int i = 0; i < producersNo; i++) {
            PaxosWorker worker = new SimpleQueueProducer(messagingContext, i % 2 == 0 ? q1 : q2, i, argsParser.getProductsNo());
            workers.add(worker);
            threads.add(new Thread(worker));
        }

        int consumersNo = argsParser.getConsumersNo();
        for (int i = 0; i < consumersNo; i++) {
            PaxosWorker worker = new EmptyTwoQueueConsumer(q1, q2, i);
            workers.add(worker);
            threads.add(new Thread(worker));
        }

        long executionTime = threadsRunner(threads);

        LinkedHashMap<String, Long> threadExecutionTimes = collectWorkersExecutionTimes(workers);

        Statistics sumStats = q1.getCollectedStatistics()
                .add(q2.getCollectedStatistics());
        for (PaxosWorker worker : workers) {
            sumStats = sumStats.add(worker.getCollectedStatistics());
        }
        return new RoundStatistics(sumStats, executionTime, threadExecutionTimes, csvPrefix);
    }
}
