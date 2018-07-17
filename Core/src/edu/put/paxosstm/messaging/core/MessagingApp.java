package edu.put.paxosstm.messaging.core;

import soa.paxosstm.dstm.PaxosSTM;

public abstract class MessagingApp {

    protected final MessagingContext messagingContext;
    protected final int nodeId;
    private final PaxosSTM paxos;

    public MessagingApp() {
        this.messagingContext = new MessagingContext();
        this.paxos = PaxosSTM.getInstance();
        this.nodeId = paxos.getId();
    }

    final void runApplication(String[] params) {
        barrier("start_application");
        application(params);
        barrier("end_application");
    }

    /**
     * Entry point of messaging application. Implement it to start processing in PaxosMessaging.
     *
     * @param params Parameters for application
     */
    public abstract void application(String[] params);


    protected void barrier(String barrierName) {
        paxos.enterBarrier(barrierName, paxos.getNumberOfNodes());
    }

    /**
     * Log for specific node. Printing '[:nodeId]: <@'
     * @param log
     */
    protected void log(String log) {
        System.out.println(String.format("[%d]: %s", nodeId, log));
    }


}
