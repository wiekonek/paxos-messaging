package edu.put.paxosstm.messaging.examples;

import edu.put.paxosstm.messaging.MessagingConfig;
import edu.put.paxosstm.messaging.MessagingEnvironment;

public class Main {
    public static void main(String[] args) throws Throwable {
        if (args.length < 1) throw new IllegalArgumentException("Not enough parameters. [nodeId]");
        int nodeId = Integer.parseInt(args[0]);
        MessagingConfig config = new MessagingConfig(nodeId)
                .withNode(0, "localhost", 2050, 3050)
                .withNode(1, "localhost", 2052, 3052);
        MessagingEnvironment.startEnvironment(config, App.class);
    }
}
