package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queues.MQueue;

public class MessagingSystemProducerWorker extends PaxosWorker {


    private final MQueue queue;

    public MessagingSystemProducerWorker(MQueue queue, int workerThreadId) {
        super(workerThreadId);
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            queue.SendMessage(new Message(getId() + ": " + i));
        }
    }
}
