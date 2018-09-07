package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.queue.QueueSelectionStrategy;
import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import soa.paxosstm.dstm.DualModeTransaction;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.Random;

class MultiMessageQueue extends MessageQueue {
    private final TBidirectionalMessageList[] tMessageLists;

    private final int queueNo;
    private int currentQueue;
    private final Random rnd;
    private final QueueSelectionStrategy queueSelectionStrategy;

    MultiMessageQueue(String id, int concurrentQueueNumber, QueueSelectionStrategy strategy) {
        super(10);
        queueSelectionStrategy = strategy;
        rnd = new Random();
        queueNo = concurrentQueueNumber;
        currentQueue = 0;
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
                        TBidirectionalMessageList list = new TBidirectionalMessageList();
                        paxos.addToSharedObjectRegistry(listId, list);
                    }
                }
            }
        };
        this.tMessageLists = new TBidirectionalMessageList[queueNo];
        for (int i = 0; i < concurrentQueueNumber; i++) {
            this.tMessageLists[i] = (TBidirectionalMessageList) PaxosSTM.getInstance().getFromSharedObjectRegistry(ids[i]);
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

    // TODO: introduce timeout / max retry number
    @Override
    public Message receiveMessage() {
        final Message[] msg = new Message[1];
        new CoreTransaction() {
            @Override
            public void atomic() {
                msg[0] = tMessageLists[currentQueue].Dequeue();
                currentQueue = nextQueueNo(currentQueue);
                if(msg[0] == null) {
                    retry();
                }
            }
        };
        return msg[0];
    }


    @Override
    protected Message getNextMessage() {

        final Message[] msg = new Message[1];
        msg[0] = tMessageLists[currentQueue].Dequeue();
        currentQueue = nextQueueNo(currentQueue);
//
//        new CoreTransaction() {
//            int r = 0;
//
//            @Override
//            public void atomic() {
//                msg[0] = tMessageLists[currentQueue].Dequeue();
//                currentQueue = nextQueueNo(currentQueue);
//                if(msg[0] == null) {
//                    if(r < queueNo) {
//                        r++;
//                        retry();
//                    } else {
//                        rollback();
//                    }
//                }
//            }
//        };
        return msg[0];
    }



    private int nextQueueNo(int no) {
        switch (queueSelectionStrategy) {
            case RoundRobin:
                return (no + 1) % queueNo;
            case Random:
                return rnd.nextInt(queueNo);
        }
        return 0;
    }
}
