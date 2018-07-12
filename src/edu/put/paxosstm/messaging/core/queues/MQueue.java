package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;

public interface MQueue {
    void SendMessage(Message msg); //or RegisterProducer
    void RegisterConsumer(MessageConsumer consumer);
}
