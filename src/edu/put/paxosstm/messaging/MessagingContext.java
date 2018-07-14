package edu.put.paxosstm.messaging;

import edu.put.paxosstm.messaging.core.queues.MQueue;
import edu.put.paxosstm.messaging.core.queues.SynchronousMessageQueue;
import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class MessagingContext {
    // TODO: What we need there : (

    /**
     * Available types of queues
     */
    public enum QueueType {

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


    /**
     * Available types of topics
     */
    public enum TopicType {
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
                    TBidirectionalMessageList list = new TBidirectionalMessageList();
                    paxos.addToSharedObjectRegistry(id, list);
                }
            }
        };
        TBidirectionalMessageList transactionalHelper = (TBidirectionalMessageList) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
        return new SynchronousMessageQueue(transactionalHelper);
    }


    /**
     *
     *
     * @param atomicAction action to perform inside global transaction
     * @param <T> that may be simple {@link Runnable} or {@link TransactionBody}
     */
    public <T extends Runnable> void globalTransaction(T atomicAction) {
        new Transaction() {
            @Override
            public void atomic() {
                atomicAction.run();
            }
        };
    }

    // TODO: Implement using existing transaction from PaxosSTM
    public static abstract class TransactionBody implements Runnable {
        protected final void commit() {
            System.out.println("commit");
        }

        protected final void rollback() {
            System.out.println("rollback");
        }

        protected final void abort() {
            System.out.println("abort");
        }
    }

}
