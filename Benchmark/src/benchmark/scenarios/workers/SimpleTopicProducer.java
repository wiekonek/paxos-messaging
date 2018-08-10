package benchmark.scenarios.workers;

import benchmark.core.BenchmarkMessagingContext;
import edu.put.paxosstm.messaging.core.MessagingContext;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.topics.MTopic;

public class SimpleTopicProducer extends PaxosWorker {
    private final MessagingContext context;
    private final MTopic topic;
    private final int productsCount;

    public SimpleTopicProducer(BenchmarkMessagingContext context, MTopic topic, int workerThreadId, int productsCount) {
        super(workerThreadId);
        this.context = context;
        this.topic = topic;
        this.productsCount = productsCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < productsCount; i++) {
            Message msg = new Message(getId() + ": " + i);
            topic.publish(msg);
        }
    }
}
