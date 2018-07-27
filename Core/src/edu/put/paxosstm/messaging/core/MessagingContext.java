package edu.put.paxosstm.messaging.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.queue.MQueue;
import soa.paxosstm.dstm.Transaction;

public class MessagingContext {


    MessagingContext() {
    }

    /**
     * Create message queue of given type with specified params.
     *
     * @param identifier Unique name of queue (identifying queue instance across all nodes).
     * @param params Parameters for creating {@link MQueue}
     * @return Return queue identified by specific name.
     */
    public MQueue createQueue(String identifier, MQueueParams params) throws MessagingException {
        String id = identifier + "_" + params.getType();

        switch (params.getType()) {
            case Simple:
                return new SingleMessageQueue(id);
            case Multi:
                return new MultiMessageQueue(id, params.getConcurrentQueueNumber());
        }

        throw new MessagingException("Unidentified queue type");
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

