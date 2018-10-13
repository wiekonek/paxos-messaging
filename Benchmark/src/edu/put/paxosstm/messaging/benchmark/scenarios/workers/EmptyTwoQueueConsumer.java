package edu.put.paxosstm.messaging.benchmark.scenarios.workers;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.MQueue;

public class EmptyTwoQueueConsumer extends PaxosWorker {

    private final MQueue q1;
    private final MQueue q2;
    private final int tListHelpers;

    public EmptyTwoQueueConsumer(MQueue q1, MQueue q2, int workerThreadId, int tListHelpers) {
        super(workerThreadId);
        this.q1 = q1;
        this.q2 = q2;
        this.tListHelpers = tListHelpers;
    }

    @Override
    protected void measuredRun() {
        int retry = 0;
        while (true) {
            final Message[] m1 = new Message[1];
            final Message[] m2 = new Message[1];

            new CoreTransaction() {
                int retryTrans = 0;

                @Override
                public void atomic() {
                    m1[0] = q1.receiveMessage();
                    m2[0] = q2.receiveMessage();


                    if (m1[0] == null || m2[0] == null) {
                        if (retryTrans < 5) {
                            retryTrans++;
                            retry();
                        } else {
                            m1[0] = null;
                            m2[0] = null;
                            rollback();
                        }
                    }
                }
            };

            if (m1[0] == null || m2[0] == null) {
                if (retry < 4 * tListHelpers) {
                    retry++;
                    try {
                        if(retry % tListHelpers == 0)
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }
}
