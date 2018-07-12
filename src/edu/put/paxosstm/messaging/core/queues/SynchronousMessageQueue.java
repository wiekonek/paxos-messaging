package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TMessageQueueHelper;
import soa.paxosstm.dstm.Transaction;

public class SynchronousMessageQueue implements MQueue {
    private final TMessageQueueHelper helper;

    public SynchronousMessageQueue(TMessageQueueHelper transactionalHelper) {
        helper = transactionalHelper;
    }

    @Override
    public void SendMessage(Message msg) {
        new Transaction() {
            @Override
            public void atomic() {
                helper.SendMessage(msg);
            }
        };
    }

    @Override
    public void RegisterConsumer(MessageConsumer messageConsumer) {
        new Transaction() {
            @Override
            public void atomic() {
                helper.AddConsumer(messageConsumer);
            }
        };
    }
}
