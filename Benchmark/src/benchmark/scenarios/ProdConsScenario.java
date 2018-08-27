package benchmark.scenarios;

import benchmark.Scenario;
import benchmark.scenarios.workers.SimpleQueueConsumer;
import benchmark.scenarios.workers.SimpleQueueProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;


public class ProdConsScenario extends Scenario {


    public ProdConsScenario(int roundsNo, String[] args) {
        super(roundsNo, args);

    }

    protected void round() throws MessagingException {

        MessageQueue queue = messagingContext.createQueueWithStatisticsCollection(
                "messages-queue",
                new MQueueParams(MQueueType.Multi, 6)
        );
        barrier("init-round");

        int producersNo = 2;
        Thread[] producers = new Thread[producersNo];
        for (int i = 0; i < producersNo; i++) {
            producers[i] = new Thread(new SimpleQueueProducer(messagingContext, queue, i, 100));
            producers[i].start();
        }

        barrier("init-producers");

        int consumersNo = 2;
        Thread[] consumers = new Thread[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            consumers[i] = new Thread(new SimpleQueueConsumer(queue, i));
            consumers[i].start();
        }

        try {
            for (Thread t : consumers) t.join();
            for (Thread t : producers) t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print(queue.getCollectedStatistics().getStatisticsLog());
        barrier("stop-round");
    }


}
