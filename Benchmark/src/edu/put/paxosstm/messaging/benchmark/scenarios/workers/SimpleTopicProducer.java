package edu.put.paxosstm.messaging.benchmark.scenarios.workers;

import edu.put.paxosstm.messaging.benchmark.core.BenchmarkMessagingContext;
import edu.put.paxosstm.messaging.MessagingContext;
import edu.put.paxosstm.messaging.data.Message;
import edu.put.paxosstm.messaging.topics.MTopic;

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
    public void measuredRun() {
        for (int i = 0; i < productsCount; i++) {
            Message msg = new Message(getId() + ": " + i);
            topic.publish(msg);
        }
    }
}
