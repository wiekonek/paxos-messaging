package edu.put.paxosstm.messaging.core;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import soa.paxosstm.dstm.PaxosSTM;

public abstract class MessagingApp {

    /**
     * Unique id of node.
     */
    protected final int nodeId;
    protected final MessagingContext messagingContext;
    protected final PaxosSTM paxos;
    protected int getNodesNo() {
        return paxos.getNumberOfNodes();
    }

    public MessagingApp() {
        this.messagingContext = new MessagingContext();
        this.paxos = PaxosSTM.getInstance();
        this.nodeId = paxos.getId();
    }

    final void runApplication(String[] params) throws MessagingException, InterruptedException {
        barrier("start_application");
        application(params);
        barrier("end_application");
    }

    /**
     * Entry point of messaging application. Implement it to start processing in PaxosMessaging.
     *
     * @param params Parameters for application
     */
    public abstract void application(String[] params) throws InterruptedException, MessagingException;


    protected void barrier(String barrierName) {
        paxos.enterBarrier(barrierName, paxos.getNumberOfNodes());
    }

    /**
     * Log for specific node. Printing '[{@link #nodeId}]: {@literal log}'
     * @param log Message to log.
     */
    protected void log(String log) {
        System.out.println(String.format("[%d]: %s", nodeId, log));
    }


}
