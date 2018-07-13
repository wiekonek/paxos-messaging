package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TMessageQueueHelper;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class SynchronousMessageQueue implements QueueFacade {
    private final TMessageQueueHelper helper;

    public SynchronousMessageQueue(TMessageQueueHelper transactionalHelper) {
        helper = transactionalHelper;
    }

    @Override
    public void sendMessage(Message msg) {
        new Transaction() {
            @Override
            public void atomic() {
                helper.SendMessage(msg);
            }
        };
    }

    @Override
    public void registerConsumer(MessageConsumer messageConsumer) {
        new Transaction() {
            @Override
            public void atomic() {
                helper.AddConsumer(messageConsumer);
            }
        };
    }

    @Override
    public String NodeId() {
        return Integer.toString(PaxosSTM.getInstance().getId());
    }
}
