package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.queue.QueueSelectionStrategy;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;

public class MQueueParams {

    /**
     * Type of {@link MQueue}
     */
    private MQueueType type = MQueueType.Simple;

    /**
     * Number of concurrent queue for {@link edu.put.paxosstm.messaging.core.MultiMessageQueue}
     */
    private int concurrentQueueNumber = 4;

    /**
     * Strategy for message queue when selecting next consumer
     */
    private QueueSelectionStrategy selectionStrategy = QueueSelectionStrategy.RoundRobin;

    /**
     * @return  See {@link #type}
     */
    MQueueType getType() {
        return type;
    }

    /**
     * @return  See {@link #concurrentQueueNumber}
     */
    int getConcurrentQueueNumber() {
        return concurrentQueueNumber;
    }

    /**
     * @return  See {@link #selectionStrategy}
     */
    QueueSelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
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
       this(type);
        this.concurrentQueueNumber = concurrentQueueNumber;
    }

    public MQueueParams(MQueueType type, int concurrentQueueNumber, QueueSelectionStrategy strategy) {
        this(type, concurrentQueueNumber);
        this.selectionStrategy = strategy;
    }


}
