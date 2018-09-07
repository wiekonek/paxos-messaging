package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.consumers.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;

import java.sql.SQLOutput;

public abstract class MessageQueue extends TransactionStatisticsCollector implements MQueue {

    private final int maxRetryNumber;

    MessageQueue(int maxRetryNumber) {
        this.maxRetryNumber = maxRetryNumber;
    }

    @Override
    public final void runConsumer(MessageConsumer messageConsumer) {
        int retryNo = 0;
        int sleepTime = 100;
        System.out.println("registering consumer");
        while (true) {
            final Message[] msg = new Message[1];
            new CoreTransaction() {
                @Override
                public void atomic() {
                    msg[0] = getNextMessage();
                }
            };
            if (msg[0] == null) {
                retryNo++;
                System.out.printf("Null message: sleep %d; retry: %d\n", sleepTime, retryNo);
                try {
                    Thread.sleep(sleepTime);
                    sleepTime += 100;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (retryNo > maxRetryNumber) {
                    break;
                }
            } else {
                messageConsumer.consumeMessage(msg[0]);
                retryNo = 0;
                sleepTime = 100;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected abstract Message getNextMessage();

}
