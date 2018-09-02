package benchmark.scenarios.workers;

import benchmark.core.BenchmarkMessagingContext;
import edu.put.paxosstm.messaging.core.MessagingContext;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.MQueue;

public class SimpleQueueProducer extends PaxosWorker {


    private final MessagingContext context;
    private final MQueue queue;
    private final int productsCount;

    public SimpleQueueProducer(BenchmarkMessagingContext context, MQueue queue, int workerThreadId, int productsCount) {
        super(workerThreadId);
        this.context = context;
        this.queue = queue;
        this.productsCount = productsCount;
    }

    @Override
    public void measuredRun() {
        for (int i = 0; i < productsCount; i++) {
            Message msg = new Message(getId() + ": " + i);
            queue.sendMessage(msg);
        }
    }
}
