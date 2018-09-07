package edu.put.paxosstm.messaging.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.topics.MTopic;
import edu.put.paxosstm.messaging.core.utils.TransactionStatisticsCollector;
import soa.paxosstm.dstm.TransactionStatistics;

public class MessagingContext extends TransactionStatisticsCollector {

    /**
     * Create message queue of given type with specified params.
     *
     * @param identifier Unique name of queue (identifying queue instance across all nodes).
     * @param params     Parameters for creating {@link MQueue}
     * @return Return queue identified by specific name.
     */
    public MQueue createQueue(String identifier, MQueueParams params) throws MessagingException {
        return createQueue(identifier, params, false);
    }

    public MTopic createTopic(String identifier) throws MessagingException {
        return createTopic(identifier, false);
    }

    public void transactionAction(Runnable action) {
        new CoreTransaction() {
            @Override
            public void atomic() {
                action.run();
            }
        };
    }

    protected MessageQueue createQueue(String identifier, MQueueParams params, boolean collectStatistics) throws MessagingException {
        String id = identifier + "_" + params.getType();
        MessageQueue queue;
        switch (params.getType()) {
            case Simple:
                queue = new SingleMessageQueue(id);
                break;
            case Multi:
                queue = new MultiMessageQueue(id, params.getConcurrentQueueNumber(), params.getSelectionStrategy());
                break;
            default:
                throw new MessagingException("Unidentified queue type");
        }
        queue.collectStatistics = collectStatistics;
        return queue;
    }

    protected  MessageTopic createTopic(String identifier, boolean collectStatistics)throws MessagingException  {
        MessageTopic topic = new MessageTopic(identifier);
        topic.collectStatistics = collectStatistics;
        return topic;
    }
}

