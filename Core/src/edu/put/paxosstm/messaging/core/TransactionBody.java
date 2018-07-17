package edu.put.paxosstm.messaging.core;

public abstract class TransactionBody implements Runnable {
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
