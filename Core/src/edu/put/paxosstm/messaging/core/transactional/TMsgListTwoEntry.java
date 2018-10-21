package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.data.Message;
import soa.paxosstm.dstm.TransactionObject;

@TransactionObject
public class TMsgListTwoEntry implements TMsgList {
    private TMessageNode _head;
    private TMessageNode _last;

    public TMsgListTwoEntry() {
        _head = null;
        _last = null;
    }

    public Message Dequeue() {
        if(_last == null) return null;
        TMessageNode tmp = _last;
        if(_head == _last) {
            _head = null;
        }
        _last = _last.next;
        return tmp.message;
    }

    public void Enqueue(Message msg) {
        TMessageNode n = new TMessageNode(msg, null, _head);
        if(_head == null) {
            _last = n;
        }
        if (_head != null) {
            _head.next = n;
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
                builder.insert(1, (tmp.prev != null ? ", " : "") + tmp.message);
                tmp = tmp.prev;
            }
            builder.append("]");
        }
        return builder.toString();
    }
}
