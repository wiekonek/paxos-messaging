package edu.put.paxosstm.messaging.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.ConsumerSelectionStrategy;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MessageQueue extends TransactionStatisticsCollector implements MQueue {

    private final List<MessageConsumer> consumers;
    private final Random rnd;
    private int consumerNo;
    private int currentConsumer;
    private final int maxRetryNumber;
    private final ConsumerSelectionStrategy consumerSelectionStrategy;

    MessageQueue(int maxRetryNumber, ConsumerSelectionStrategy consumerSelectionStrategy) {
        this.maxRetryNumber = maxRetryNumber;
        this.consumerSelectionStrategy = consumerSelectionStrategy;
        consumers = new ArrayList<>();
        currentConsumer = 0;
        consumerNo = 0;
        rnd = new Random();
    }

    @Override
    public final void registerConsumer(MessageConsumer messageConsumer) {
        synchronized (this) {
            consumers.add(messageConsumer);
            consumerNo++;
        }
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
                if(consumers.size() > currentConsumer) {
                    MessageConsumer consumer;
                    synchronized (consumers) {
                        consumer = consumers.get(currentConsumer);
                    }
                    if (consumer != null) {
                        consumer.consumeMessage(msg[0]);
                    }
                }
                switch (consumerSelectionStrategy) {
                    case RoundRobin:
                        currentConsumer = (currentConsumer + 1) % consumerNo;
                        break;
                    case Random:
                        currentConsumer = rnd.nextInt(consumerNo);
                }
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
