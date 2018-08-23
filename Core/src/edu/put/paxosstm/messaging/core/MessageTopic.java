package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.data.MessageWithIndex;
import edu.put.paxosstm.messaging.core.topics.MTopic;
import edu.put.paxosstm.messaging.core.transactional.TTopicHelper;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class MessageTopic extends TransactionStatisticsCollector implements MTopic {

    private final TTopicHelper tHelper;
    private final int maxRetryNumber;

    MessageTopic(String id) {
        this(id, 3);
    }

    MessageTopic(String id, int maxRetryNumber) {
        this.maxRetryNumber = maxRetryNumber;
        new Transaction() {
            @Override
            public void atomic() {
                PaxosSTM paxos = PaxosSTM.getInstance();
                if (paxos.getFromSharedObjectRegistry(id) == null) {
                    TTopicHelper list = new TTopicHelper();
                    paxos.addToSharedObjectRegistry(id, list);
                }
            }
        };
        tHelper = (TTopicHelper) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
    }

    @Override
    public void publish(Message msg) {
        new CoreTransaction() {
            @Override
            public void atomic() {
                tHelper.add(msg);
            }
        };
    }

    @Override
    public void registerSubscriber(MessageConsumer subscriber) {
        Thread thread = new Thread(() -> {
            final MessageWithIndex[] msg = new MessageWithIndex[1];

            new CoreTransaction(true) {
                int retryNumber = 0;

                @Override
                public void atomic() {
                    retryNumber++;
                    msg[0] = tHelper.getNewest();
                    if (msg[0] == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (retryNumber > maxRetryNumber) rollback();

                        retry();
                    }
                }
            };


            final int[] index = {msg[0].getIndex()};
            subscriber.consumeMessage(msg[0]);

            while (true) {
                msg[0] = null;
                new CoreTransaction(true) {
                    int retryNumber = 0;

                    @Override
                    public void atomic() {
                        retryNumber++;
                        index[0]++;
                        msg[0] = tHelper.get(index[0]);
                        if (msg[0] == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (retryNumber > maxRetryNumber) rollback();
                            index[0]--;
                            retry();
                        }
                    }

                };
                if (msg[0] == null) break;

                subscriber.consumeMessage(msg[0]);
            }

        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
