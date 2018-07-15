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
        for (int i = 0; i < 3; i++) {
            System.out.println("Round: " + i);
            round();
        }
        PaxosSTM.getInstance().enterBarrier("end", params.nodesNumber);
    }

    private void round() throws InterruptedException {

        MessagingContext context = new MessagingContext();
        MQueue queue = context.createQueue("messages-queue");
        PaxosSTM.getInstance().enterBarrier("init", params.nodesNumber);



        int producersNo = 2;
        Thread[] producers = new Thread[producersNo];
        for (int i = 0; i < producersNo; i++) {
            producers[i] = new Thread(new MessagingSystemProducerWorker(context, queue, i));
            producers[i].start();
        }

        PaxosSTM.getInstance().enterBarrier("init_producers", params.nodesNumber);

        int consumersNo = 2;
        Thread[] consumers = new Thread[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            consumers[i] = new Thread(new MessagingSystemConsumerWorker(queue, i));
            consumers[i].start();
        }

        for (Thread t : consumers) {
            t.join();
        }
        PaxosSTM.getInstance().enterBarrier("stop", params.nodesNumber);
    }
}
