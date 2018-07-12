package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.core.MessageLogger;
import edu.put.paxosstm.messaging.core.queues.MQueue;

import java.io.Serializable;


public class MessagingSystemConsumerWorker extends PaxosWorker implements Serializable {
    private final MQueue queue;

    public MessagingSystemConsumerWorker(MQueue queue, int workerThreadId) {
        super(workerThreadId);
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.RegisterConsumer(new MessageLogger(getId()));
    }
}
