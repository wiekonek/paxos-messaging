package edu.put.paxosstm.messaging.core.topics;

import edu.put.paxosstm.messaging.core.data.Message;

public class FullyTransactionalTopic implements MTopic {
    @Override
    public void publish(Message msg) {

    }

    @Override
    public Message subscribe() {
        return null;
    }

}
