package edu.put.paxosstm.messaging.examples;

import edu.put.paxosstm.messaging.consumers.MessageLogger;
import edu.put.paxosstm.messaging.core.MessagingApp;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queues.MQueue;

public class App extends MessagingApp {

    @Override
    public void application(String[] params) {
        MQueue queue = messagingContext.createQueue("test-queue");
        Thread[] threads = new Thread[2];
        threads[0] = new Thread(() -> queue.registerConsumer(new MessageLogger(String.format("[%d]", nodeId))));
        threads[1] = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                queue.sendMessage(new Message(String.format("Hello world message from [%d] with value: %d", nodeId, i)));
            }
        });
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
