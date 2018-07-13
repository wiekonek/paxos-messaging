package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.MessageConsumer;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queues.QueueApi;
import soa.paxosstm.dstm.Transaction;
import soa.paxosstm.dstm.TransactionObject;
import soa.paxosstm.utils.TransactionalArrayList;

import java.io.Serializable;


@TransactionObject
public class TMessageQueueHelper {

    @TransactionObject
    private int currentConsumer = 0;

    private TransactionalArrayList<MessageConsumer> consumers;

    public static class NodeRef implements Serializable {
        public QueueApi api;
        public int nodeId;
    }

    public NodeRef nodeRef = new NodeRef();

    public TMessageQueueHelper() {
        consumers = new TransactionalArrayList<>();
    }

    public void SendMessage(Message msg) {
        new Transaction() {
            @Override
            public void atomic() {
                if (consumers.isEmpty())
                    return;
                if(nodeRef.api != null) {
                    System.out.println("node: [" + nodeRef.api.NodeId() + "]");
                }
                consumers.get(currentConsumer).consumeMessage(msg);
                currentConsumer = (currentConsumer + 1) % consumers.size();
            }
        };
    }

    public void AddConsumer(MessageConsumer consumer) {
        consumers.add(consumer);
    }
}
