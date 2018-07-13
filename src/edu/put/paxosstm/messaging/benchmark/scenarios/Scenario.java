package edu.put.paxosstm.messaging.benchmark.scenarios;

import soa.paxosstm.common.CheckpointListener;
import soa.paxosstm.common.Storage;
import soa.paxosstm.dstm.PaxosSTM;

import java.io.Externalizable;

public abstract class Scenario {

    private final Object commitLock = new Object();


    public void run() throws InterruptedException {
        initCommitListener();

        System.out.println("--- Start benchmark!");
        runBenchmark(PaxosSTM.getInstance().getId() == 0);
        System.out.println("--- End benchmark!");
        makeSnapshot();
    }

    protected abstract  void runBenchmark(boolean isMaster) throws InterruptedException;

    protected void makeSnapshot() {
        synchronized (commitLock) {
            PaxosSTM.getInstance().scheduleCheckpoint();
            try {
                commitLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initCommitListener() {
        PaxosSTM.getInstance().addCheckpointListener(new CheckpointListener() {
            @Override
            public void onCheckpoint(int seqNumber, Storage storage) {
            }

            @Override
            public void onCheckpointFinished(int seqNumber) {
                synchronized (commitLock) {
                    commitLock.notifyAll();
                }
            }
        });
    }
}
