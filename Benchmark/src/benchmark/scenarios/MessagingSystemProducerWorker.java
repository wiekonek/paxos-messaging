package benchmark.scenarios;

import edu.put.paxosstm.messaging.core.MessagingContext;
import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queues.MQueue;

public class MessagingSystemProducerWorker extends PaxosWorker {


    private final MessagingContext context;
    private final MQueue queue;

    public MessagingSystemProducerWorker(MessagingContext context, MQueue queue, int workerThreadId) {
        super(workerThreadId);
        this.context = context;
        this.queue = queue;
    }

    @Override
    public void run() {
        context.globalTransaction(() -> {
            for (int i = 0; i < 10; i++) {
                Message msg = new Message(getId() + ": " + i);
                queue.sendMessage(msg);
            }
        });
    }
}
