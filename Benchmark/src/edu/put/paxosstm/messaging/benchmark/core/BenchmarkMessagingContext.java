package edu.put.paxosstm.messaging.benchmark.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.*;
import soa.paxosstm.dstm.TransactionStatistics;

public class BenchmarkMessagingContext extends MessagingContext {

    public MessageQueue createQueueWithStatisticsCollection(String identifier, MQueueParams params) throws MessagingException {
        return createQueue(identifier, params, true);
    }

    public MessageTopic createTopicWithStatisticsCollection(String identifier) throws MessagingException {
        return createTopic(identifier, true);
    }
}
