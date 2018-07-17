package edu.put.paxosstm.messaging.consumers;

import edu.put.paxosstm.messaging.core.data.Message;

public class MessageLogger implements  MessageConsumer {

    private final String prefix;

    public MessageLogger(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void consumeMessage(Message msg) {
        System.out.println(prefix + ": Consuming: " + msg);
    }
}
