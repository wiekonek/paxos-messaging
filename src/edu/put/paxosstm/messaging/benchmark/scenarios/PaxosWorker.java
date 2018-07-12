package edu.put.paxosstm.messaging.benchmark.scenarios;

import soa.paxosstm.dstm.PaxosSTM;

public abstract class PaxosWorker implements  Runnable {

    protected final int nodeId;
    protected final int workerThreadId;

    public PaxosWorker(int workerThreadId) {
        this.nodeId = PaxosSTM.getInstance().getId();
        this.workerThreadId = workerThreadId;
    }

    protected String getId() {
        return String.format("[%1$02d, %2$02d]", nodeId, workerThreadId);
    }

    protected void log(String str) {
        System.out.println(String.format("%s: %s", getId(), str));
    }
}
