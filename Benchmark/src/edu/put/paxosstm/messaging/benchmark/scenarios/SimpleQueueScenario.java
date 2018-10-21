package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.benchmark.RoundStatistics;
import edu.put.paxosstm.messaging.benchmark.Scenario;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.PaxosWorker;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.EmptyQueueConsumer;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.SimpleQueueProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.MQueueParams;
import edu.put.paxosstm.messaging.MessageQueue;
import edu.put.paxosstm.messaging.queue.MQueueType;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class SimpleQueueScenario extends Scenario {
    protected final ProdConsQueueParser argsParser;
    protected final String csvPrefix;

    public SimpleQueueScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
        argsParser = new ProdConsQueueParser(args);
        csvPrefix = String.format(
                "%d,%d,%d,%d,%d,%s,%s",
                nodesNo,
                // TODO: Maybe I should introduce final variables for these fields
                argsParser.getProducersNo(),
                argsParser.getConsumersNo(),
                argsParser.getProductsNo(),
                argsParser.getConcurrentQueueNo(),
                argsParser.getSelectionStrategy(),
                argsParser.getMsgListType()
        );
    }

    protected RoundStatistics round(int round) throws MessagingException {

        MessageQueue queue = messagingContext.createQueueWithStatisticsCollection(
                "messages-queue",
                new MQueueParams(
                        MQueueType.Multi,
                        argsParser.getConcurrentQueueNo(),
                        argsParser.getSelectionStrategy(),
                        argsParser.getRetryNumber(),
                        argsParser.getRetryDelay(),
                        argsParser.getMsgListType()
                )
        );
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<PaxosWorker> workers = new ArrayList<>();
        barrier(String.format("init-round-%d", round));


        int producersNo = argsParser.getProducersNo();
        for (int i = 0; i < producersNo; i++) {
            PaxosWorker worker = new SimpleQueueProducer(messagingContext, queue, i, argsParser.getProductsNo());
            workers.add(worker);
            threads.add(new Thread(worker));
        }

        int consumersNo = argsParser.getConsumersNo();
        for (int i = 0; i < consumersNo; i++) {
            PaxosWorker worker = new EmptyQueueConsumer(queue, i);
            workers.add(worker);
            threads.add(new Thread(worker));
        }

        long executionTime = threadsRunner(threads) - (argsParser.getRetryDelay() * argsParser.getRetryNumber());

        LinkedHashMap<String, Long> threadExecutionTimes = collectWorkersExecutionTimes(workers);

        return new RoundStatistics(queue.getCollectedStatistics(), executionTime, threadExecutionTimes, csvPrefix);
    }
}
