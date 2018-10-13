package edu.put.paxosstm.messaging.benchmark.scenarios.workers;

import edu.put.paxosstm.messaging.core.queue.MQueue;

public class EmptyQueueConsumer extends PaxosWorker {
    private final MQueue queue;

    public EmptyQueueConsumer(MQueue queue, int workerThreadId) {
        super(workerThreadId);
        this.queue = queue;
    }

    @Override
    public void measuredRun() {
        queue.runConsumer((ignore) -> {});
    }
}
