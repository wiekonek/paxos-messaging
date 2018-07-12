package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TMessageQueueOnBidirectionalList;
import soa.paxosstm.dstm.Transaction;

public class FullyTransactionalQueue implements MQueue {

    private final TMessageQueueOnBidirectionalList _paxosQueue;

    public FullyTransactionalQueue(TMessageQueueOnBidirectionalList paxosQueue) {
        _paxosQueue = paxosQueue;
    }

    public void Enqueue(Message msg) {
        new Transaction() {
            @Override
            public void atomic() {
                _paxosQueue.Enqueue(msg);
            }
        };
    }

    public Message Dequeue() {
        // TODO: I don't know what I'm doing
        final Message[] msg = new Message[1];
        new Transaction() {
            @Override
            public void atomic() {
                msg[0] = _paxosQueue.Dequeue();
            }
        };

        return msg[0];
    }

    public String toString() {
        // TODO: I don't know what I'm doing
        final String[] res = new String[1];
        new Transaction() {
            @Override
            public void atomic() {
                res[0] = _paxosQueue.toString();
            }
        };
        return res[0];
    }
}
