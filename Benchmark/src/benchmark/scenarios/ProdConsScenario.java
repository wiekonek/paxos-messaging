package benchmark.scenarios;

import benchmark.RoundStatistics;
import benchmark.Scenario;
import benchmark.scenarios.workers.PaxosWorker;
import benchmark.scenarios.workers.EmptyQueueConsumer;
import benchmark.scenarios.workers.SimpleQueueProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class ProdConsScenario extends Scenario {
    private final ProdConsQueueParser argsParser;

    public ProdConsScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
        argsParser = new ProdConsQueueParser(args);
    }

    protected RoundStatistics round(int round) throws MessagingException {

        MessageQueue queue = messagingContext.createQueueWithStatisticsCollection(
                "messages-queue",
                new MQueueParams(MQueueType.Multi, argsParser.getConcurrentQueueNo(), argsParser.getSelectionStrategy())
        );
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<PaxosWorker> workers = new ArrayList<>();
        barrier(String.format("init-round-%d", round));

        if(nodeId % 2 == 0) {
            int producersNo = argsParser.getProducersNo();
            for (int i = 0; i < producersNo; i++) {
                PaxosWorker worker = new SimpleQueueProducer(messagingContext, queue, i, argsParser.getProductsNo());
                workers.add(worker);
                threads.add(new Thread(worker));
            }
        } else {
            int consumersNo = argsParser.getConsumersNo();
            for (int i = 0; i < consumersNo; i++) {
                PaxosWorker worker = new EmptyQueueConsumer(queue, i);
                workers.add(worker);
                threads.add(new Thread(worker));
            }
        }
        long executionTime = threadsRunner(threads);

        LinkedHashMap<String, Long> threadExecutionTimes = collectWorkersExecutionTimes(workers);

        return new RoundStatistics(queue.getCollectedStatistics(), executionTime, threadExecutionTimes);
    }
}
