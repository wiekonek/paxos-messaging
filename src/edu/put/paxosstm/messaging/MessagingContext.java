package edu.put.paxosstm.messaging;

import edu.put.paxosstm.messaging.core.queues.MQueue;
import edu.put.paxosstm.messaging.core.queues.SynchronousMessageQueue;
import edu.put.paxosstm.messaging.core.transactional.TMessageQueueHelper;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class MessagingContext {
    // TODO: What we need there : (

    /**
     * Type of queue
     */
    public enum QueueType {
        FullyTransactional {
            @Override
            public String toString() {
                return "fully-transactional";
            }
        },
        Synchronous {
            @Override
            public String toString() {
                return "synchronous";
            }
        },
        Simplest {
            @Override
            public String toString() {
                return "simplest";
            }
        }
    }

    public enum TopicType {
        FullyTransactional {
            @Override
            public String toString() {
                return "fully-transactional";
            }
        },
        Simplest {
            @Override
            public String toString() {
                return "simplest";
            }
        }
    }


    public MessagingContext() {
    }


    /**
     * Create default type {@link MessagingContext.QueueType#Synchronous} message queue
     *
     * @param identifier Unique name of queue (identifying queue instance across all nodes)
     * @return Return queue identified by specific name
     */
    public MQueue createQueue(String identifier) {
        return  createQueue(identifier, QueueType.Synchronous);
    }

    /**
     * Create message queue of given type
     *
     * @param identifier Unique name of queue (identifying queue instance across all nodes)
     * @param type Type of queue
     * @return Return queue identified by specific name
     */
    public MQueue createQueue(String identifier, QueueType type) {
        String id = identifier + type;
        new Transaction() {
            @Override
            public void atomic() {
                PaxosSTM paxos = PaxosSTM.getInstance();

                if (paxos.getFromSharedObjectRegistry(id) == null) {
                    TMessageQueueHelper queue = new TMessageQueueHelper();
                    paxos.addToSharedObjectRegistry(id, queue);
                }
            }
        };
        TMessageQueueHelper transactionalHelper = (TMessageQueueHelper) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
        return new SynchronousMessageQueue(transactionalHelper);
    }


}
