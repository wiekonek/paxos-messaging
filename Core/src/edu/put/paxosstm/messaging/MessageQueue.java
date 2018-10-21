package edu.put.paxosstm.messaging;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.data.Message;
import edu.put.paxosstm.messaging.queue.MQueue;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;

public abstract class MessageQueue extends TransactionStatisticsCollector implements MQueue {

    @Override
    public void runConsumer(MessageConsumer messageConsumer) {
        while (true) {
            Message msg = getMessage();
            if (msg == null) {
                break;
            }
            messageConsumer.consumeMessage(msg);
        }
    }

    protected abstract Message getMessage();

}
