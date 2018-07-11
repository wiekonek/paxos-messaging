package edu.put.paxosmessaging.core.transactional;

import edu.put.paxosmessaging.core.data.Message;
import soa.paxosstm.dstm.TransactionObject;


@TransactionObject
public class TMessageQueue {

    @TransactionObject
    private class Node {
        public Message message;
        public Node next;
        public Node prev;

        private Node(Message message, Node next, Node prev) {
            this.message = message;
            this.next = next;
            this.prev = prev;
        }

    }

    private Node _head;



    public TMessageQueue() {
        _head = null;
    }

    public Message Dequeue() {
        if(_head == null) {
            return null;
        }
        Node tmp = _head;
        while(tmp.next != null) {
            tmp = tmp.next;
        }
        if(tmp.prev != null) {
            tmp.prev.next = null;
        } else {
            _head = null;
        }
        return tmp.message;
    }

    public void Enqueue(Message msg) {
        Node n = new Node(msg, _head, null);
        if(_head != null) {
            _head.prev = n;
        }
        _head = n;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(_head == null) {
            builder.append("Empty");
        } else {
            builder.append("[");
            Node tmp = _head;

            while(tmp != null) {
                builder.insert(1, (tmp.next != null ? ", " : "") + tmp.message);
                tmp = tmp.next;
            }
            builder.append("]");
        }
        return builder.toString();
    }
}
