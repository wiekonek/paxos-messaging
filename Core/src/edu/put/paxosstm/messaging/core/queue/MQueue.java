package edu.put.paxosstm.messaging.core.queue;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.utils.MStatistics;

public interface MQueue extends MStatistics {
    void sendMessage(Message msg);
    Message receiveMessage();
    void registerConsumer(MessageConsumer consumer);
}
