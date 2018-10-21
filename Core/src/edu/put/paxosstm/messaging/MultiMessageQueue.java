package edu.put.paxosstm.messaging;

import edu.put.paxosstm.messaging.data.Message;
import edu.put.paxosstm.messaging.queue.QueueSelectionStrategy;
import edu.put.paxosstm.messaging.core.transactional.TMsgList;
import edu.put.paxosstm.messaging.core.transactional.TMsgListFactory;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

import java.util.Random;

class MultiMessageQueue extends MessageQueue {
    private final TMsgList[] tMessageLists;

    private final int concurrentQueueNumber;
    private final Random rnd;
    private final QueueSelectionStrategy queueSelectionStrategy;
    private final int maxRetryNumber;
    private final int retryDelay;

    private int currentQueue;

    MultiMessageQueue(String id, MQueueParams params) {

        maxRetryNumber = params.getRetryNumber();
        retryDelay = params.getRetryDelay();
        queueSelectionStrategy = params.getSelectionStrategy();
        concurrentQueueNumber = params.getConcurrentQueueNumber();
        currentQueue = 0;
        rnd = new Random();
        String[] ids = new String[concurrentQueueNumber];
        for (int i = 0; i < concurrentQueueNumber; i++) {
            ids[i] = id + "_" + i;
        }
        new Transaction() {
            @Override
            public void atomic() {
                for (String listId : ids) {
                    PaxosSTM paxos = PaxosSTM.getInstance();
                    if (paxos.getFromSharedObjectRegistry(listId) == null) {
                        TMsgList list = TMsgListFactory.create(params.getBiMsgListType());
                        paxos.addToSharedObjectRegistry(listId, list);
                    }
                }
            }
        };
        this.tMessageLists = new TMsgList[concurrentQueueNumber];
        for (int i = 0; i < concurrentQueueNumber; i++) {
            this.tMessageLists[i] = (TMsgList) PaxosSTM.getInstance().getFromSharedObjectRegistry(ids[i]);
        }
    }

    @Override
    public int sendMessage(Message msg) {
        int tListNumber = currentQueue;
        new CoreTransaction() {
            @Override
            public void atomic() {
                tMessageLists[tListNumber].Enqueue(msg);
            }
        };

        currentQueue = nextQueueNo(currentQueue);
        return tListNumber;
    }

    @Override
    public int sendMessage(Message msg, int tListNumber) {
        new CoreTransaction() {
            @Override
            public void atomic() {
                tMessageLists[tListNumber % concurrentQueueNumber].Enqueue(msg);
            }
        };
        return tListNumber;
    }

    @Override
    public Message receiveMessage() {
        final Message[] msg = new Message[1];
        new CoreTransaction() {
            @Override
            public void atomic() {
                msg[0] = tMessageLists[currentQueue].Dequeue();
                currentQueue = nextQueueNo(currentQueue);
            }
        };
        return msg[0];
    }


    @Override
    protected Message getMessage() {
        return getMessage(maxRetryNumber, retryDelay);
    }

    protected Message getMessage(int retryNo, int retryDelay) {
        final Message[] msg = new Message[1];

        new CoreTransaction() {
            int retry = 0;

            @Override
            public void atomic() {
                msg[0] = tMessageLists[currentQueue].Dequeue();
                currentQueue = nextQueueNo(currentQueue);
                if (msg[0] == null) {
                    if (retry < retryNo * concurrentQueueNumber) {
                        retry++;
                        if (retry % concurrentQueueNumber == 0) {
                            try {
                                Thread.sleep(retryDelay); // TODO: Przenieść na zewnątrz
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        retry();
                    } else {
                        rollback();
                    }
                }
            }
        };

        return msg[0];
    }

    private int nextQueueNo(int no) {
        switch (queueSelectionStrategy) {
            case RoundRobin:
                return (no + 1) % concurrentQueueNumber;
            case Random:
                return rnd.nextInt(concurrentQueueNumber);
        }
        return 0;
    }
}
