package edu.put.paxosstm.messaging;

import soa.paxosstm.dstm.Transaction;

public abstract class MessagingTransaction extends Transaction {
    public MessagingTransaction() {
        super();
    }

    public MessagingTransaction(boolean readOnly) {
        super(readOnly);
    }
}
