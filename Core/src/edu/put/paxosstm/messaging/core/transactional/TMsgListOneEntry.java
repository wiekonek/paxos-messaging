package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.data.Message;
import soa.paxosstm.dstm.TransactionObject;


@TransactionObject
public class TMsgListOneEntry implements TMsgList {

    private TMessageNode _head;

    public TMsgListOneEntry() {
        _head = null;
    }

    public Message Dequeue() {
        if (_head == null) return null;

        TMessageNode tmp = _head;

        while (tmp.next != null) {
            tmp = tmp.next;
        }

        if (tmp.prev == null) {
            _head = null;
        } else {
            tmp.prev.next = null;
        }

        return tmp.message;
    }

    public void Enqueue(Message msg) {
        TMessageNode n = new TMessageNode(msg, _head, null);
        if (_head != null) {
            _head.prev = n;
        }
        _head = n;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (_head == null) {
            builder.append("Empty");
        } else {
            builder.append("[");
            TMessageNode tmp = _head;

            while (tmp != null) {
                builder.insert(1, (tmp.next != null ? ", " : "") + tmp.message);
                tmp = tmp.next;
            }
            builder.append("]");
        }
        return builder.toString();
    }
}
