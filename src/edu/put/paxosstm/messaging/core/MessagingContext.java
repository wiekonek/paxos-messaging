package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.queues.MQueue;
import edu.put.paxosstm.messaging.core.queues.SynchronousMessageQueue;
import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class MessagingContext {

    /**
     * Available types of queues
     */
    public enum QueueType {
        Simple {
            @Override
            public String toString() {
                return "simple";
            }
        },
    }

    /**
     * Available types of topics
     */
    public enum TopicType {
        Simple {
            @Override
            public String toString() {
                return "simple";
            }
        }
    }

    MessagingContext() {
    }

    /**
     * Create default type {@link MessagingContext.QueueType#Simple} message queue.
     *
     * @param identifier Unique name of queue (identifying queue instance across all nodes).
     * @return Return queue identified by specific name.
     */
    public MQueue createQueue(String identifier) {
        return createQueue(identifier, QueueType.Simple);
    }

    /**
     * Create message queue of given type.
     *
     * @param identifier Unique name of queue (identifying queue instance across all nodes).
     * @param type Type of queue.
     * @return Return queue identified by specific name.
     */
    public MQueue createQueue(String identifier, QueueType type) {
        String id = identifier + "_" + type;
        new Transaction() {
            @Override
            public void atomic() {
                PaxosSTM paxos = PaxosSTM.getInstance();
                if (paxos.getFromSharedObjectRegistry(id) == null) {
                    TBidirectionalMessageList list = new TBidirectionalMessageList();
                    paxos.addToSharedObjectRegistry(id, list);
                }
            }
        };
        TBidirectionalMessageList transactionalHelper = (TBidirectionalMessageList) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
        return new SynchronousMessageQueue(transactionalHelper);
    }


    /**
     * Inside this method you can create transaction.
     *
     * @param atomicAction Action to perform inside global transaction.
     * @param <T> Type may be simple {@link Runnable} or {@link TransactionBody}.
     */
    public <T extends Runnable> void globalTransaction(T atomicAction) {
        new Transaction() {
            @Override
            public void atomic() {
                atomicAction.run();
            }
        };
    }


}

