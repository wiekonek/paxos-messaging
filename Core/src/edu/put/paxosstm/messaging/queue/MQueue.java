package edu.put.paxosstm.messaging.queue;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.data.Message;

public interface MQueue {
    int sendMessage(Message msg);

    int sendMessage(Message msg, int tListId);

    /**
     * @return May return null
     */
    Message receiveMessage();

    void runConsumer(MessageConsumer consumer);
}
