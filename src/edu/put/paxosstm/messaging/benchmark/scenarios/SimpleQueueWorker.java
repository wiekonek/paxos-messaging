package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TMessageQueueOnBidirectionalList;
import soa.paxosstm.dstm.Transaction;

public class SimpleQueueWorker extends PaxosWorker {
    private TMessageQueueOnBidirectionalList _queue;

    public SimpleQueueWorker(TMessageQueueOnBidirectionalList tQueue, int threadId) {
        super(threadId);
        _queue = tQueue;
    }

    @Override
    public void run() {
        new Transaction() {
            @Override
            public void atomic() {
                _queue.Enqueue(new Message(getId() + ": " + "Hello"));
            }
        };
    }
}
