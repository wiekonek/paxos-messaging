package edu.put.paxosstm.messaging.examples;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.consumers.MessageLogger;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessagingApp;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;

public class App extends MessagingApp {

    @Override
    public void application(String[] params) {
        try {
            MQueue queue = messagingContext.createQueue("test-queue", new MQueueParams(MQueueType.Simple, 10));

            Thread[] threads = new Thread[3];
            threads[0] = new Thread(() -> queue.registerConsumer(new MessageLogger(String.format("[%d]", nodeId))));
            threads[1] = new Thread(() -> {
                for (int i = 0; i < 50; i++) {
                    queue.sendMessage(new Message(String.format("<<{%d} {%d}>>", nodeId, i)));
                }
            });
            for (Thread thread : threads) {
                if (thread != null) {
                    thread.start();
                }
            }

            for (Thread thread : threads) {
                if (thread != null) {
                    thread.join();
                }
            }

            Thread.sleep(1000);
        } catch (MessagingException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
