package benchmark.scenarios.workers;

import edu.put.paxosstm.messaging.core.topics.MTopic;

public class SimpleSubscriber extends PaxosWorker {

    private final MTopic topic;

    public SimpleSubscriber(MTopic topic, int workerThreadId) {
        super(workerThreadId);
        this.topic = topic;
    }

    @Override
    public void measuredRun() {
        topic.registerSubscriber(msg -> log(msg.toString()));
    }
}
