package benchmark.scenarios;

import benchmark.Scenario;
import benchmark.scenarios.workers.SimpleSubscriber;
import benchmark.scenarios.workers.SimpleTopicProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MessageTopic;
public class ProdConsTopicScenario extends Scenario {


    public ProdConsTopicScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
    }

    protected void round() throws MessagingException {

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

        try {
            for (Thread t : subscribers) t.join();
            for (Thread t : producers) t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print(topic.getCollectedStatistics().getStatisticsLog());
        barrier("stop-round");
    }

}
