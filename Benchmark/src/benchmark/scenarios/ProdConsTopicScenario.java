package benchmark.scenarios;

import benchmark.config.BasicScenarioParameters;
import benchmark.core.Scenario;
import benchmark.scenarios.workers.SimpleSubscriber;
import benchmark.scenarios.workers.SimpleTopicProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MessageTopic;
import tools.Tools;

public class ProdConsTopicScenario extends Scenario {
    private BasicScenarioParameters params;

    @Override
    protected void benchmark(String[] params) throws InterruptedException, MessagingException {
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

        MessageTopic topic = messagingContext.createTopicWithStatisticsCollection("messages-topic");
        barrier("init-round");

        int producersNo = 2;
        Thread[] producers = new Thread[producersNo];
        for (int i = 0; i < producersNo; i++) {
            producers[i] = new Thread(new SimpleTopicProducer(messagingContext, topic, i, 2));
        }

        int consumersNo = 2;
        Thread[] subscribers = new Thread[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            subscribers[i] = new Thread(new SimpleSubscriber(topic, i));
        }

        for(Thread t : subscribers) t.start();
        for(Thread t : producers) t.start();

        for (Thread t : subscribers) t.join();
        for (Thread t : producers) t.join();

        System.out.printf(topic.getCollectedStatistics().getStatisticsLog());
        barrier("stop-round");
    }
}
