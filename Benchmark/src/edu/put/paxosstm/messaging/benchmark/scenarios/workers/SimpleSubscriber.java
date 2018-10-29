package edu.put.paxosstm.messaging.benchmark.scenarios.workers;

import edu.put.paxosstm.messaging.topics.MTopic;

public class SimpleSubscriber extends PaxosWorker {

    private final MTopic topic;

    public SimpleSubscriber(MTopic topic, int workerThreadId) {
        super(workerThreadId);
        this.topic = topic;
    }

    @Override
    public void measuredRun() {
        topic.registerSubscriber(msg -> {/*log(msg.toString());*/}, true);
    }
}
