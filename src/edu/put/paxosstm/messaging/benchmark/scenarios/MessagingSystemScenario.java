package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.benchmark.config.BasicScenarioParameters;
import edu.put.paxosstm.messaging.MessagingContext;
import edu.put.paxosstm.messaging.core.queues.MQueue;
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
        for (int i = 0; i < 4; i++) {
            System.out.println("Round: " + i);
            round();
        }
        PaxosSTM.getInstance().enterBarrier("end", params.nodesNumber);
    }

    private void round() throws InterruptedException {

        MessagingContext context = new MessagingContext();
        MQueue queue = context.createQueue("messages-queue");


        Thread[] threads = new Thread[4];
        threads[0] = new Thread(new MessagingSystemConsumerWorker(queue, 0));
        threads[1] = new Thread(new MessagingSystemConsumerWorker(queue, 1));
        threads[0].start();
        threads[1].start();
        PaxosSTM.getInstance().enterBarrier("init", params.nodesNumber);

        for (int i = 2; i < 4; i++) {
            threads[i] = new Thread(new MessagingSystemProducerWorker(context, queue, i));
        }
        for(int i = 2; i < 4; i++) {
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }

        PaxosSTM.getInstance().enterBarrier("stop", params.nodesNumber);
    }
}
