package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;

public interface MQueue {
    void sendMessage(Message msg); //or RegisterProducer
    void registerConsumer(MessageConsumer consumer);
}
