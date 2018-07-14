package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.dstm.TransactionStatistics;

public class SynchronousMessageQueue implements MQueue {
    private final TBidirectionalMessageList tMessageList;

    public SynchronousMessageQueue(TBidirectionalMessageList tMessageList) {
        this.tMessageList = tMessageList;
    }

    @Override
    public void sendMessage(Message msg) {
        new Transaction() {
            @Override
            public void atomic() {
                tMessageList.Enqueue(msg);
            }
        };
    }

    @Override
    public void registerConsumer(MessageConsumer messageConsumer) {
        final Message[] msg = new Message[1];
        final boolean[] rollback = {false};
        while (true) {
            new Transaction() {
                int retryNo = 0;
                @Override
                public void atomic() {
                    retryNo++;
                    msg[0] = tMessageList.Dequeue();
                    if (msg[0] == null) {
                        if(retryNo >= 3) {
                            rollback[0] = true;
                            rollback();
                        }
                        retry();
                    }
                }
            };
            if(rollback[0]) {
                break;
            }
            messageConsumer.consumeMessage(msg[0]);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
