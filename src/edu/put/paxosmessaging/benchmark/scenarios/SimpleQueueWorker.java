package edu.put.paxosmessaging.benchmark.scenarios;

import edu.put.paxosmessaging.core.data.Message;
import edu.put.paxosmessaging.core.transactional.TMessageQueue;
import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.utils.TransactionalQueue;

public class SimpleQueueWorker implements Runnable {
    private TMessageQueue _queue;

    public SimpleQueueWorker(TMessageQueue tQueue) {
        _queue = tQueue;
    }

    @Override
    public void run() {
        new Transaction() {
            @Override
            public void atomic() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(">>> " + i);
                    _queue.Enqueue(new Message(i));
                }
            }
        };
//        new Transaction() {
//            @Override
//            public void atomic() {
//                for (int i = 0; i < 2; i++) {
//                    System.out.println("<<< " + _queue.Dequeue());
//                }
//            }
//        };
//        new Transaction() {
//            @Override
//            public void atomic() {
//                for (int i = 5; i < 10; i++) {
//                    System.out.println(">>> " + i);
//                    _queue.Enqueue(new Message(i));
//                }
//            }
//        };
    }
}
