package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;

public abstract class MessageQueue extends TransactionStatisticsCollector implements MQueue {

    @Override
    public void runConsumer(MessageConsumer messageConsumer) {
        while (true) {
            Message msg = receiveMessage();
            if (msg == null) {
                break;
            }
            messageConsumer.consumeMessage(msg);
        }
    }

}
