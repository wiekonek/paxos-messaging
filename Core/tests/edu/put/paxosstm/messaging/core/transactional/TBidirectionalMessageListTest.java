package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.data.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TBidirectionalMessageListTest {

    private TBidirectionalMessageList queue;

    @BeforeEach
    void init() {
        queue = new TBidirectionalMessageList();
    }

    @Test
    void dequeue_returnNull_ifEmptyQueue() {
        assertNull(queue.Dequeue());
    }

    @Test
    void dequeue_returnMessage_ifQueueHasOneMessage() {
        Message msg = new Message("data");
        queue.Enqueue(msg);
        assertEquals(msg, queue.Dequeue());
    }

    @Test
    void dequeue_returnMessagesInFifoOrder() {
        Message m1 = new Message("0");
        Message m2 = new Message("1");
        queue.Enqueue(m1);
        queue.Enqueue(m2);
        assertEquals(m1, queue.Dequeue());
        assertEquals(m2, queue.Dequeue());
    }

    @Test
    void dequeue_returnNull_ifQueueHadRemovedAllMessages() {
        Message msg = new Message("data");
        queue.Enqueue(msg);
        queue.Dequeue();
        assertNull(queue.Dequeue());
    }

    @Test
    void enqueue_successfullyAddMessage_ifEmptyQueue() {
        Message message = new Message("0");
        queue.Enqueue(message);
    }

    @Test
    void enqueue_successfullyAddMessage_ifNotEmptyQueue() {
        Message m1 = new Message("0");
        Message m2 = new Message("1");
        queue.Enqueue(m1);
        queue.Enqueue(m2);
    }

    @Test
    void toString_commaSepparatedMessages_ifNotEmptyQueue() {
        Message m1 = new Message("0");
        Message m2 = new Message("1");
        queue.Enqueue(m1);
        queue.Enqueue(m2);
        assertEquals("[" + m1 + ", " + m2 + "]", queue.toString());
    }

    @Test
    void toString_oneMessage_ifQueueHasOneMessage() {
        Message m1 = new Message("0");
        queue.Enqueue(m1);
        assertEquals("[" + m1 + "]", queue.toString());
    }

    @Test
    void toString_returnEmpty_ifQueueIsEmpty() {
        assertEquals("Empty", queue.toString());
    }

}