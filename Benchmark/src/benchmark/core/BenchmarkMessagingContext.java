package benchmark.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.core.MessagingContext;
import edu.put.paxosstm.messaging.core.MessagingTransaction;
import soa.paxosstm.dstm.TransactionStatistics;

public class BenchmarkMessagingContext extends MessagingContext {

    public MessageQueue createQueueWithStatisticsCollection(String identifier, MQueueParams params) throws MessagingException {
        return (MessageQueue) super.createQueue(identifier, params, true);
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
