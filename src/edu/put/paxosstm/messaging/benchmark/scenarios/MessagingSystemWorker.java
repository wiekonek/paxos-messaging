package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.MessagingContext;
import edu.put.paxosstm.messaging.core.data.Message;

public class MessagingSystemWorker extends PaxosWorker {
    private final MessagingContext context;
    private MessageQueue queue;

    public MessagingSystemWorker(MessagingContext context, int threadId) {
        super(threadId);
        this.context = context;
        this.queue = context.createQueue("messaging-queue");
    }


    @Override
    public void run() {
        queue.Enqueue(new Message(getId() + ": 0"));
        log("Received message: " + queue.Dequeue());
        context.globalTransaction(new MessagingContext.TransactionBody() {
            @Override
            public void run() {
                log("Trans start: queue" + queue.toString());
                queue.Enqueue(new Message(getId() + "t"));
                log(queue.Dequeue().toString());
                log("Trans end: queue" + queue.toString());
            }
        });
        context.globalTransaction(() -> {
            log("Trans start: queue" + queue.toString());
            queue.Enqueue(new Message(getId() + "t"));
            log(queue.Dequeue().toString());
            log("Trans end: queue" + queue.toString());
        });
    }
}
