package edu.put.paxosmessaging.core.transactional;

import soa.paxosstm.dstm.TransactionObject;

import java.util.LinkedList;
import java.util.Queue;


@TransactionObject
public class TQueue {
    private Queue<Integer> _queue;

    public TQueue() {
        _queue = new LinkedList<>();
    }

    public Integer Dequeue() {
        return _queue.poll();
    }

    public void Enqueue(Integer i) {
        _queue.add(i);
    }

    @Override
    public String toString() {
        StringBuilder queueStringBuilder = new StringBuilder();
        for(Integer i : _queue) {
            queueStringBuilder.append(i + " ");
        }
        return "TQueue{" +
                "_queue=" + queueStringBuilder.toString()  +
                '}';
    }
}
