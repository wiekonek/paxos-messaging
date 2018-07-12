package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.dstm.TransactionObject;
import soa.paxosstm.utils.TransactionalArrayList;


@TransactionObject
public class TMessageQueueHelper {

    @TransactionObject
    public int currentConsumer = 0;

    private TransactionalArrayList<MessageConsumer> consumers;

    public TMessageQueueHelper() {
        consumers = new TransactionalArrayList<>();
    }

    public void SendMessage(Message msg) {
        new Transaction() {
            @Override
            public void atomic() {
                if (consumers.isEmpty())
                    return;

                consumers.get(currentConsumer).consumeMessage(msg);
                currentConsumer = (currentConsumer + 1) % consumers.size();
            }
        };
    }

    public void AddConsumer(MessageConsumer consumer) {
        consumers.add(consumer);
    }
}
