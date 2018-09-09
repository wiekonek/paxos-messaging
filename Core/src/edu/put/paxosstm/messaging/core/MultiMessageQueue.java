package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.QueueSelectionStrategy;
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
    public void sendMessage(Message msg) {

        new CoreTransaction() {
            @Override
            public void atomic() {
                tMessageLists[currentQueue].Enqueue(msg);
            }
        };

        currentQueue = nextQueueNo(currentQueue);
    }


    @Override
    public Message receiveMessage() {
        return receiveMessage(maxRetryNumber, retryDelay);
    }

    @Override
    public Message receiveMessage(int retryNo, int retryDelay) {
        final Message[] msg = new Message[1];

        new CoreTransaction() {
            int retry = 0;

            @Override
            public void atomic() {
                msg[0] = tMessageLists[currentQueue].Dequeue();
                currentQueue = nextQueueNo(currentQueue);
                if (msg[0] == null) {
                    if (retry < retryNo * maxRetryNumber) {
                        retry++;
                        if (retry % retryNo == 0) {
                            try {
                                Thread.sleep(retryDelay);
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
