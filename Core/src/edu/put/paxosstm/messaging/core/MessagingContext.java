package edu.put.paxosstm.messaging.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.queue.MQueue;

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


    public void transactionAction(Runnable action) {
        new MessagingTransaction() {
            @Override
            public void atomic() {
                action.run();
            }
        };
    }


}

