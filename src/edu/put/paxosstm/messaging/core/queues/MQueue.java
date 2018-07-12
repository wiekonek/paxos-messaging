package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.data.Message;

public interface MQueue {
    void Enqueue(Message msg);
    Message Dequeue();
    String toString();
}
