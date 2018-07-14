package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import soa.paxosstm.dstm.Transaction;

public class SimpleQueueWorker extends PaxosWorker {
    private TBidirectionalMessageList _queue;

    public SimpleQueueWorker(TBidirectionalMessageList tQueue, int threadId) {
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
