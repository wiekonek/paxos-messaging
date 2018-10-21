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
                        index[0] = msg[0].getIndex();
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

    @Override
    public void clean() {
        System.out.println("Cleaning!!!");
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
