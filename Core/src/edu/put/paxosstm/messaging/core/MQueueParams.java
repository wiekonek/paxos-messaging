package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;

public class MQueueParams {

    private static final int DEFAULT_CONCURRENT_QUEUE = 4;
    private static final MQueueType DEFAULT_QUEUE_TYPE = MQueueType.Simple;

    /**
     * Type of {@link MQueue}
     */
    private MQueueType type = MQueueType.Simple;

    /**
     * Number of concurrent queue for {@link edu.put.paxosstm.messaging.core.MultiMessageQueue}
     */
    private int concurrentQueueNumber = 4;

    MQueueType getType() {
        return type;
    }

    int getConcurrentQueueNumber() {
        return concurrentQueueNumber;
    }

    /**
     * Default parameters for creation of {@link MQueue}.
     *
     * Create {@link MQueueType#Simple} MQueue.
     */
    public MQueueParams() {

    }

    public MQueueParams(MQueueType type) {
        this.type = type;
    }

    public MQueueParams(MQueueType type, int concurrentQueueNumber) {
        this.type = type;
        this.concurrentQueueNumber = concurrentQueueNumber;
    }
}
