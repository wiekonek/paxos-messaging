package edu.put.paxosstm.messaging.core.topics;

import edu.put.paxosstm.messaging.core.data.Message;

public interface MTopic {
    void publish(Message msg);
    Message subscribe();
}
