package benchmark.scenarios;

import edu.put.paxosstm.messaging.core.queues.MQueue;
import edu.put.paxosstm.messaging.consumers.MessageLogger;

import java.io.Serializable;


public class MessagingSystemConsumerWorker extends PaxosWorker implements Serializable {
    private final MQueue queue;

    public MessagingSystemConsumerWorker(MQueue queue, int workerThreadId) {
        super(workerThreadId);
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.registerConsumer(new MessageLogger(getId()));
    }
}
