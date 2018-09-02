package benchmark.scenarios;

import benchmark.RoundStatistics;
import benchmark.Scenario;
import benchmark.scenarios.workers.*;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MessageTopic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProdConsTopicScenario extends Scenario {
    private final ProdConsParser argsParser;


    public ProdConsTopicScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
        argsParser = new ProdConsParser(args);
    }

    protected RoundStatistics round() throws MessagingException {
        MessageTopic topic = messagingContext.createTopicWithStatisticsCollection("messages-topic");
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
        return new RoundStatistics(topic.getCollectedStatistics(), executionTime, threadExecutionTimes);
    }

}
