package benchmark.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.*;
import soa.paxosstm.dstm.TransactionStatistics;

public class BenchmarkMessagingContext extends MessagingContext {

    public MessageQueue createQueueWithStatisticsCollection(String identifier, MQueueParams params) throws MessagingException {
        return super.createQueue(identifier, params, true);
    }

    public MessageTopic createTopicWithStatisticsCollection(String identifier) throws MessagingException {
        return super.createTopic(identifier, true);
    }

    @Override
    public void transactionAction(Runnable action) {
        new MessagingTransaction() {
            @Override
            public void atomic() {
                action.run();
            }

            @Override
            public void statistics(TransactionStatistics statistics) {
                super.statistics(statistics);
            }
        };
    }
}
