package edu.put.paxosstm.messaging.benchmark.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.MQueueParams;
import edu.put.paxosstm.messaging.MessageQueue;
import edu.put.paxosstm.messaging.MessageTopic;
import edu.put.paxosstm.messaging.MessagingContext;

public class BenchmarkMessagingContext extends MessagingContext {

    public MessageQueue createQueueWithStatisticsCollection(String identifier, MQueueParams params) throws MessagingException {
        return createQueue(identifier, params, true);
    }

    public MessageTopic createTopicWithStatisticsCollection(String identifier, int bufferSize) throws MessagingException {
        return createTopic(identifier, bufferSize, true);
    }
}
