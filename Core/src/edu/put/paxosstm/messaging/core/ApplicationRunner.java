package edu.put.paxosstm.messaging.core;

import edu.put.paxosstm.messaging.core.utils.OracleInitializer;
import soa.paxosstm.dstm.PaxosSTM;

import java.util.Arrays;

public class ApplicationRunner {
    public static void main(String[] args) throws Exception {
        PaxosSTM.getInstance().start();
        OracleInitializer.initDefferedOracle();

        if (args == null || args.length < 1) throw new Exception("Missing params");

        MessagingApp app = MessagingApplicationFactory.createApplication(args[0]);
        app.runApplication(args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[] {});

        PaxosSTM.getInstance().enterBarrier("exit", PaxosSTM.getInstance().getNumberOfNodes());
    }
}
