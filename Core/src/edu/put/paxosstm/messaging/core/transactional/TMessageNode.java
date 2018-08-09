package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.data.Message;
import soa.paxosstm.dstm.TransactionObject;

@TransactionObject
public class TMessageNode {
    public Message message;
    TMessageNode next;
    TMessageNode prev;

    TMessageNode(Message message, TMessageNode next, TMessageNode prev) {
        this.message = message;
        this.next = next;
        this.prev = prev;
    }

}