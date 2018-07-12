package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.benchmark.config.BasicScenarioParameters;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.MessagingContext;
import soa.paxosstm.dstm.PaxosSTM;
import tools.Tools;


public class MessagingSystemScenario extends Scenario {

    private BasicScenarioParameters params;

    public MessagingSystemScenario(String params) {
        this.params = new BasicScenarioParameters();
        try {
            this.params = (BasicScenarioParameters) Tools.fromString(params, this.params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void runBenchmark(boolean isMaster) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            System.out.println("Round: " + i);
            round();
        }
        PaxosSTM.getInstance().enterBarrier("end", params.nodesNumber);
    }

    private void round() throws InterruptedException {

        MessagingContext context = new MessagingContext();
        MessageQueue queue = context.createQueue("messages-queue");

        PaxosSTM.getInstance().enterBarrier("init", params.nodesNumber);

        Thread[] threads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            threads[i] = new Thread(new MessagingSystemWorker(context, i));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        PaxosSTM.getInstance().enterBarrier("stop", params.nodesNumber);

        System.out.println(queue);
    }
}
