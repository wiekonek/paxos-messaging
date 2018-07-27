package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;


class MultiMessageQueue extends MessageQueue {
    private final TBidirectionalMessageList[] tMessageLists;

    private final int queueNo;
    private int currentQueue;

    MultiMessageQueue(String id, int concurrentQueueNumber) {
        super(6);
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
        new Transaction() {
            @Override
            public void atomic() {
                tMessageLists[currentQueue].Enqueue(msg);
            }
        };
        currentQueue = nextQueueNo();
    }

    // TODO: introduce timeout / max retry number
    @Override
    public Message receiveMessage() {
        final Message[] msg = new Message[1];
        new Transaction() {
            @Override
            public void atomic() {
                msg[0] = tMessageLists[currentQueue].Dequeue();
                currentQueue = nextQueueNo();
                if(msg[0] == null) {
                    retry();
                }
            }
        };
        return msg[0];
    }


    @Override
    protected Message getNextMessage() {
        Message msg = tMessageLists[currentQueue].Dequeue();
        currentQueue = nextQueueNo();
        return msg;
    }

    private int nextQueueNo() {
        return (currentQueue + 1) % queueNo;
    }
}
