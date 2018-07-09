package edu.put.paxosmessaging.benchmark.scenarios;

import soa.paxosstm.common.CheckpointListener;
import soa.paxosstm.common.Storage;
import soa.paxosstm.common.StorageException;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.internal.TransactionOracle;
import soa.paxosstm.tools.Tools;

public abstract class Scenario {

    private final Object commitLock = new Object();


    public Scenario(String[] params) {
    }

    public void run() throws InterruptedException {
        initCommitListener();

        runBenchmark(PaxosSTM.getInstance().getId() == 0);

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
