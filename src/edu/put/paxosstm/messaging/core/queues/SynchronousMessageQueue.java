package edu.put.paxosstm.messaging.core.queues;

import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import soa.paxosstm.dstm.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SynchronousMessageQueue implements MQueue {
    private final TBidirectionalMessageList tMessageList;

    private final List<MessageConsumer> consumers;
    private int consumerNo;
    private int currentConsumer;

    public SynchronousMessageQueue(TBidirectionalMessageList tMessageList) {
        this.tMessageList = tMessageList;
        consumers = new ArrayList<>();
        currentConsumer = 0;
        consumerNo = 0;
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
        consumers.add(messageConsumer);
        consumerNo++;
        if (consumerNo == 1) startConsuming(100);
    }

    private void startConsuming(int sleepTime) {

        Thread thread = new Thread(() -> {
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
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (retryNo > 3) {
                                rollback[0] = true;
                                rollback();
                            }
                            retry();
                        }
                    }
                };
                if (rollback[0]) {
                    break;
                }

                consumers.get(currentConsumer).consumeMessage(msg[0]);
                currentConsumer = (currentConsumer + 1) % consumerNo;


            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
