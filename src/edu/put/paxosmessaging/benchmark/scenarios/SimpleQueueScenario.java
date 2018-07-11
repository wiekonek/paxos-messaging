package edu.put.paxosmessaging.benchmark.scenarios;

import edu.put.paxosmessaging.core.transactional.TMessageQueue;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.utils.TransactionalQueue;

public class SimpleQueueScenario extends Scenario {
    public SimpleQueueScenario(String[] params) {
        super(params);
    }

    @Override
    protected void runBenchmark(boolean isMaster) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            System.out.println("Round: " + i);
            round(isMaster);
//            System.gc();
//            System.gc();
        }
        
        // TODO: number of nodes is hardcoded there
        PaxosSTM.getInstance().enterBarrier("end", 3);
    }
    
    private void round(boolean isMaster) throws InterruptedException {
        if(isMaster) {
            new Transaction() {
                @Override
                public void atomic() {
                    TMessageQueue tQueue = new TMessageQueue();
                    PaxosSTM.getInstance().addToSharedObjectRegistry("t_queue", tQueue);
                }
            };
            makeSnapshot();
        }

        PaxosSTM.getInstance().enterBarrier("init", 3);
        makeSnapshot();

        TMessageQueue tQueue = (TMessageQueue) PaxosSTM.getInstance().getFromSharedObjectRegistry("t_queue");

        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            threads[i] = new Thread(new SimpleQueueWorker(tQueue));
        }
        for (int i = 0; i < 2; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 2; i++) {
            threads[i].join();
        }

        PaxosSTM.getInstance().enterBarrier("stop", 3);
        new Transaction() {
            @Override
            public void atomic() {
                TMessageQueue queue = (TMessageQueue) PaxosSTM.getInstance().getFromSharedObjectRegistry("t_queue");
                System.out.println(queue);
            }
        };
    }
}
