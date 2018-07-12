package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import soa.paxosstm.dstm.TransactionObject;

import java.util.ArrayList;
import java.util.List;

@TransactionObject
public class TMessageQueueHelper {

    public int currentConsumer = 0;
    public List<MessageConsumer> consumers;

    public TMessageQueueHelper() {
        consumers = new ArrayList<>();
    }

    public void SendMessage(Message msg) {
        if(consumers.isEmpty())
            return;
        consumers.get(currentConsumer).consumeMessage(msg);
        currentConsumer++;
    }

    public void AddConsumer(MessageConsumer consumer) {
        consumers.add(consumer);
    }
}
