package benchmark.scenarios.workers;

import soa.paxosstm.dstm.PaxosSTM;

public abstract class PaxosWorker implements Runnable {

    private final int nodeId;
    private final int workerThreadId;
    private long executionTime;

    PaxosWorker(int workerThreadId) {
        this.nodeId = PaxosSTM.getInstance().getId();
        this.workerThreadId = workerThreadId;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        measuredRun();
        executionTime = System.currentTimeMillis() - startTime;
    }

    public String getFullName() {
        return String.format("[%04d %04d] <%s>", nodeId, workerThreadId, getClass().getSimpleName());
    }

    public long getExecutionTime() {
        return executionTime;
    }

    void log(String str) {
        System.out.println(String.format("%s: %s", getId(), str));
    }

    protected abstract void measuredRun();

    String getId() {
        return String.format("[%04d, %04d]", nodeId, workerThreadId);
    }
}
