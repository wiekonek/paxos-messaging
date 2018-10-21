package edu.put.paxosstm.messaging.consumers;

import edu.put.paxosstm.messaging.data.Message;

import java.io.Serializable;

public interface MessageConsumer extends Serializable {
    void consumeMessage(Message msg);
}
