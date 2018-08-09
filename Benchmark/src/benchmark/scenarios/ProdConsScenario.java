package benchmark.scenarios;

import benchmark.config.BasicScenarioParameters;
import benchmark.core.Scenario;
import benchmark.scenarios.workers.SimpleQueueConsumer;
import benchmark.scenarios.workers.SimpleQueueProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;
import tools.Tools;


public class ProdConsScenario extends Scenario {

    private BasicScenarioParameters params;

    @Override
    public void benchmark(String[] params) throws InterruptedException, MessagingException {
        this.params = new BasicScenarioParameters();
        try {
            this.params = (BasicScenarioParameters) Tools.fromString(params[0], this.params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < this.params.roundsNumber; i++) {
            System.out.println();
            System.out.println("Round: " + i);
            round();
        }
    }


    private void round() throws InterruptedException, MessagingException {

        MessageQueue queue = messagingContext.createQueueWithStatisticsCollection(
                "messages-queue",
                new MQueueParams(MQueueType.Multi, 4)
        );
        barrier("init-round");

        int producersNo = 2;
        Thread[] producers = new Thread[producersNo];
        for (int i = 0; i < producersNo; i++) {
            producers[i] = new Thread(new SimpleQueueProducer(messagingContext, queue, i, 2));
            producers[i].start();
        }

        barrier("init-producers");

        int consumersNo = 2;
        Thread[] consumers = new Thread[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            consumers[i] = new Thread(new SimpleQueueConsumer(queue, i));
            consumers[i].start();
        }

        for (Thread t : consumers) {
            t.join();
        }
        for (Thread t : producers) {
            t.join();
        }

        System.out.printf(queue.getCollectedStatistics().getStatisticsLog());
        barrier("stop-round");
    }
}
