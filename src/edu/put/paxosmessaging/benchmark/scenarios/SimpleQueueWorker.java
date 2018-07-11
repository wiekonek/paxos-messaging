package edu.put.paxosmessaging.benchmark.scenarios;

import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.utils.TransactionalQueue;

public class SimpleQueueWorker implements Runnable {
    private TransactionalQueue<Integer>_queue;

    public SimpleQueueWorker(TransactionalQueue<Integer> tQueue) {
        _queue = tQueue;
    }

    @Override
    public void run() {
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(">>> " + i);
                    _queue.offer(i);
                }
            }
        };
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 0; i < 2; i++) {
                    System.out.println("<<< " + _queue.remove());
                }
            }
        };
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 5; i < 10; i++) {
                    System.out.println(">>> " + i);
                    _queue.offer(i);
                }
            }
        };
    }
}
