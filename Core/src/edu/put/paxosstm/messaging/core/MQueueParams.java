package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.queue.QueueSelectionStrategy;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;
import edu.put.paxosstm.messaging.core.transactional.TMsgListType;

public class MQueueParams {

    /**
     * Type of {@link MQueue}
     */
    private MQueueType type = MQueueType.Simple;

    /**
     * Number of concurrent queue for {@link MultiMessageQueue}
     */
    private int concurrentQueueNumber = 4;

    /**
     * Strategy for message queue when selecting next consumer
     */
    private QueueSelectionStrategy selectionStrategy = QueueSelectionStrategy.RoundRobin;

    private int retryNumber = 10;

    private int retryDelay = 100;

    private TMsgListType biMsgListType;


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

    public MQueueParams(MQueueType type, int concurrentQueueNumber, QueueSelectionStrategy strategy, int retryNumber, int retryDelay, TMsgListType biMsgListType) {
        this(type, concurrentQueueNumber);
        this.selectionStrategy = strategy;
        this.retryNumber = retryNumber;
        this.retryDelay = retryDelay;
        this.biMsgListType = biMsgListType;
    }

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

    int getRetryNumber() {
        return retryNumber;
    }

    int getRetryDelay() {
        return retryDelay;
    }

    TMsgListType getBiMsgListType() {
        return biMsgListType;
    }
}
