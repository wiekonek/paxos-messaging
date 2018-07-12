package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.data.Message;

public interface MessageConsumer {
    void consumeMessage(Message msg);
}
