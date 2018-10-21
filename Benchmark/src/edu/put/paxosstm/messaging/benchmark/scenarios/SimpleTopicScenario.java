package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.benchmark.RoundStatistics;
import edu.put.paxosstm.messaging.benchmark.Scenario;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.PaxosWorker;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.SimpleSubscriber;
import edu.put.paxosstm.messaging.benchmark.scenarios.workers.SimpleTopicProducer;
import edu.put.paxosstm.messaging.MessageTopic;
import soa.paxosstm.dstm.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SimpleTopicScenario extends Scenario {
    private final ProdConsParser argsParser;


    public SimpleTopicScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
        argsParser = new ProdConsParser(args);
    }

    protected RoundStatistics round(int round) throws MessagingException {
        MessageTopic topic = messagingContext.createTopicWithStatisticsCollection("messages-topic", 5000);
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<PaxosWorker> workers = new ArrayList<>();
        barrier("init-round");

        int producersNo = argsParser.getProducersNo();
        for (int i = 0; i < producersNo; i++) {
            PaxosWorker worker = new SimpleTopicProducer(messagingContext, topic, i, argsParser.getProductsNo());
            workers.add(worker);
            threads.add(new Thread(worker));
        }
        int consumersNo = argsParser.getConsumersNo();
        for (int i = 0; i < consumersNo; i++) {
            PaxosWorker worker = new SimpleSubscriber(topic, i);
            workers.add(worker);
            threads.add(new Thread(worker));
        }

        long executionTime = threadsRunner(threads);
        LinkedHashMap<String, Long> threadExecutionTimes = collectWorkersExecutionTimes(workers);

        barrier("stop-round");
        if(isMaster) {
            topic.clean();
            new Transaction() {
                @Override
                public void atomic() {
                    paxos.removeFromSharedObjectRegistry("messages-topic");
                }
            };
        }

        return new RoundStatistics(topic.getCollectedStatistics(), executionTime, threadExecutionTimes, "");
    }

}
