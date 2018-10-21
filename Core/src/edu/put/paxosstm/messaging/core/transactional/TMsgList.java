package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.data.Message;

public interface TMsgList {
    Message Dequeue();
    void Enqueue(Message msg);
}
