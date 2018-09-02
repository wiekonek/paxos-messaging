package benchmark.scenarios.workers;

import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.consumers.MessageLogger;

import java.io.Serializable;


public class SimpleQueueConsumer extends PaxosWorker {
    private final MQueue queue;

    public SimpleQueueConsumer(MQueue queue, int workerThreadId) {
        super(workerThreadId);
        this.queue = queue;
    }

    @Override
    public void measuredRun() {
        queue.registerConsumer((msg) -> {});
    }
}
