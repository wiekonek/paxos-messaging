package edu.put.paxosmessaging.benchmark.scenarios;

import edu.put.paxosmessaging.core.transactional.TQueue;
import soa.paxosstm.dstm.Transaction;

public class SimpleQueueWorker implements Runnable {
    private TQueue _queue;

    public SimpleQueueWorker(TQueue tQueue) {
        _queue = tQueue;
    }

    @Override
    public void run() {
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(">>> " + i);
                    _queue.Enqueue(i);
                }
            }
        };
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 0; i < 2; i++) {
                    System.out.println("<<< " + _queue.Dequeue());
                }
            }
        };
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 5; i < 10; i++) {
                    System.out.println(">>> " + i);
                    _queue.Enqueue(i);
                }
            }
        };
    }
}
