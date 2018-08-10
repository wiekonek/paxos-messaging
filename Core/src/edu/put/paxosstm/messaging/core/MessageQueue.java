package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageQueue extends TransactionStatisticsCollector implements MQueue {

    private final List<MessageConsumer> consumers;
    private int consumerNo;
    private int currentConsumer;
    private final int maxRetryNumber;

    MessageQueue() {
        this(3);
    }

    MessageQueue(int maxRetryNumber) {
        this.maxRetryNumber = maxRetryNumber;
        consumers = new ArrayList<>();
        currentConsumer = 0;
        consumerNo = 0;
    }

    @Override
    public final void registerConsumer(MessageConsumer messageConsumer) {
        consumers.add(messageConsumer);
        consumerNo++;
        if (consumerNo == 1) startConsuming(100);
    }

    protected abstract Message getNextMessage();

    private void startConsuming(int sleepTime) {

        Thread thread = new Thread(() -> {
            final Message[] msg = new Message[1];
            final boolean[] exit = {false};

            while (true) {
                new CoreTransaction() {
                    int retryNo = 0;

                    @Override
                    public void atomic() {
                        retryNo++;

                        msg[0] = getNextMessage();

                        if (msg[0] == null) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (retryNo > maxRetryNumber) {
                                exit[0] = true;
                                rollback();
                            }
                            retry();
                        }
                    }
                };
                if (exit[0]) {
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
