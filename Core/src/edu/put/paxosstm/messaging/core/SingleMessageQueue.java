package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.transactional.TBidirectionalMessageList;
import edu.put.paxosstm.messaging.core.data.Message;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;

class SingleMessageQueue extends MessageQueue {
    private final TBidirectionalMessageList tMessageList;



    SingleMessageQueue(String id) {
        super(3);
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
        this.tMessageList = (TBidirectionalMessageList) PaxosSTM.getInstance().getFromSharedObjectRegistry(id);
    }

    @Override
    public void sendMessage(Message msg) {
        new CoreTransaction() {
            @Override
            public void atomic() {
                tMessageList.Enqueue(msg);
            }
        };
    }

    @Override
    public Message receiveMessage() {
        final Message[] msg = new Message[1];
        new CoreTransaction() {
            @Override
            public void atomic() {
                msg[0] = tMessageList.Dequeue();
                if(msg[0] == null) {
                    retry();
                }
            }
        };
        return msg[0];
    }

    @Override
    protected Message getNextMessage() {
        return tMessageList.Dequeue();
    }
}
