package edu.put.paxosstm.messaging;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.data.Message;
import edu.put.paxosstm.messaging.data.MessageWithIndex;
import edu.put.paxosstm.messaging.topics.MTopic;
import edu.put.paxosstm.messaging.core.transactional.TTopicHelper;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class MessageTopic extends TransactionStatisticsCollector implements MTopic {

    private TTopicHelper tHelper;
    private final String id;
    private final int maxRetryNumber;

    MessageTopic(String id) {
        this(id, 3, 10000);
    }

    MessageTopic(String id, int maxRetryNumber, int bufferSize) {
        this.id = id;
        this.maxRetryNumber = maxRetryNumber;
        new Transaction() {
            @Override
            public void atomic() {
                PaxosSTM paxos = PaxosSTM.getInstance();
                if (paxos.getFromSharedObjectRegistry(id) == null) {
                    TTopicHelper list = new TTopicHelper(bufferSize);
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
        registerSubscriber(subscriber, false);
    }

    @Override
    public void registerSubscriber(MessageConsumer subscriber, boolean fromOldest) {
        Thread thread = new Thread(() -> {
            final int[] index = {0};

            if(!fromOldest) {
                new CoreTransaction(true) {
                    int retryNumber = 0;

                    @Override
                    public void atomic() {
                        retryNumber++;
                        MessageWithIndex msg = tHelper.getNewest();
                        if (msg == null) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (retryNumber > maxRetryNumber) rollback();

                            retry();
                        }
                        index[0] = msg.getIndex();
                        subscriber.consumeMessage(msg);
                    }
                };
            }


            final boolean[] end = {false};
            while (true) {
                end[0] = false;
                new CoreTransaction(true) {
                    int retryNumber = 0;

                    @Override
                    public void atomic() {
                        retryNumber++;
                        index[0]++;
                        MessageWithIndex msg = tHelper.get(index[0]);
                        if (msg == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (retryNumber > maxRetryNumber) {
                                end[0] = true;
                                rollback();
                            }
                            index[0]--;
                            retry();
                        }
                        index[0] = msg.getIndex();
                        subscriber.consumeMessage(msg);
                    }

                };
                if (end[0]) break;
            }

        });
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clean() {
        new Transaction() {
            @Override
            public void atomic() {
                tHelper.clean();
                PaxosSTM paxos = PaxosSTM.getInstance();
                paxos.removeFromSharedObjectRegistry(id);
                TTopicHelper list = new TTopicHelper(1000);
                paxos.addToSharedObjectRegistry(id, list);
            }
        };
        tHelper = (TTopicHelper) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
    }
}
