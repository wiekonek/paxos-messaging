package edu.put.paxosstm.messaging.core.queue;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;

public interface MQueue {
    void sendMessage(Message msg);
    Message receiveMessage();
    Message receiveMessage(int retryNo, int retryDelay);
    void runConsumer(MessageConsumer consumer);
}
