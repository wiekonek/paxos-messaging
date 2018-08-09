package benchmark.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import soa.paxosstm.dstm.PaxosSTM;

public abstract class Scenario {

    protected final BenchmarkMessagingContext messagingContext;
    protected final PaxosSTM paxos;
    protected final int nodeId;

    public Scenario() {
        this.messagingContext = new BenchmarkMessagingContext();
        this.paxos = PaxosSTM.getInstance();
        this.nodeId = paxos.getId();
    }

    protected abstract void benchmark(String[] params) throws InterruptedException, MessagingException;

    protected void barrier(String barrierName) {
        paxos.enterBarrier(barrierName, paxos.getNumberOfNodes());
    }

    void run(String[] params) throws InterruptedException, MessagingException {
        barrier("start-scenario");
        System.out.println("--- Start scenario benchmark!");
        benchmark(params);
        barrier("end-scenario");
        System.out.println("--- End benchmark!");
    }
}
