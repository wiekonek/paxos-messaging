package edu.put.paxosstm.messaging;

import edu.put.paxosstm.messaging.core.queues.FullyTransactionalQueue;
import edu.put.paxosstm.messaging.core.queues.MQueue;
import edu.put.paxosstm.messaging.core.topics.MTopic;
import edu.put.paxosstm.messaging.core.transactional.TMessageQueueOnBidirectionalList;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

public class MessagingContext {
    // TODO: What we need there : (
    public enum QueueType {
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

    public MQueue createQueue(String identifier) {
        return  createQueue(identifier, QueueType.FullyTransactional);
    }


    public MQueue createQueue(String identifier, QueueType type) {
        String id = identifier + type;
        new Transaction() {
            @Override
            public void atomic() {
                PaxosSTM paxos = PaxosSTM.getInstance();

                if (paxos.getFromSharedObjectRegistry(id) == null) {
                    TMessageQueueOnBidirectionalList queue = new TMessageQueueOnBidirectionalList();
                    paxos.addToSharedObjectRegistry(id, queue);
                }
            }
        };
        TMessageQueueOnBidirectionalList paxosQueue = (TMessageQueueOnBidirectionalList) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
        return new FullyTransactionalQueue(paxosQueue);
    }

    public MTopic createTopic(String identifier) {
        return createTopic(identifier, TopicType.FullyTransactional);
    }

    public MTopic createTopic(String identifier, TopicType type) {
        return null;
    }

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
        protected void commit() {
            System.out.println("commit");
        }

        protected void rollback() {
            System.out.println("rollback");
        }

        protected void abort() {
            System.out.println("abort");
        }
    }

}
