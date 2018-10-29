package edu.put.paxosstm.messaging.topics;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.data.Message;

public interface MTopic {
    void publish(Message msg);
    void registerSubscriber(MessageConsumer subscriber);
    void registerSubscriber(MessageConsumer subscriber, boolean fromOldest);
    void clean();
}
