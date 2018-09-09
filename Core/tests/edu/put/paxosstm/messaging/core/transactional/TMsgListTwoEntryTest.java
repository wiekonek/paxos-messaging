package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.data.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TMsgListTwoEntryTest {

    private TMsgListTwoEntry queue;

    @BeforeEach
    void init() {
        queue = new TMsgListTwoEntry();
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
        Message m0 = new Message("0");
        Message m1 = new Message("1");
        queue.Enqueue(m0);
        queue.Enqueue(m1);
        assertEquals(m0, queue.Dequeue());
        assertEquals(m1, queue.Dequeue());
    }

    @Test
    void dequeue_returnMessagesInFifoOrder_ifEnqueueAndDequeueInterweave() {
        Message m0 = new Message("0");
        Message m1 = new Message("1");
        Message m2 = new Message("2");
        Message m3 = new Message("3");
        Message m4 = new Message("4");
        queue.Enqueue(m0);
        queue.Enqueue(m1);
        assertEquals(m0, queue.Dequeue());
        assertEquals(m1, queue.Dequeue());
        queue.Enqueue(m2);
        assertEquals(m2, queue.Dequeue());
        queue.Enqueue(m3);
        queue.Enqueue(m4);
        assertEquals(m3, queue.Dequeue());
        assertEquals(m4, queue.Dequeue());
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