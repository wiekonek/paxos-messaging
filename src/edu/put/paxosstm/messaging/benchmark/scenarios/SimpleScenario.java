package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.benchmark.config.SimpleScenarioParameters;
import edu.put.paxosstm.messaging.core.transactional.TInt;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;
import tools.Tools;

import java.io.IOException;

public class SimpleScenario extends Scenario {
    private final SimpleScenarioParameters _params;

    public SimpleScenario(String params) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        SimpleScenarioParameters p = new SimpleScenarioParameters();
        _params = (SimpleScenarioParameters) Tools.fromString(params, p);


    }

    @Override
    protected void runBenchmark(boolean isMaster) throws InterruptedException {

        for (int i = 0; i < 2; i++) {
            System.out.println("Round: " + i);
            round(isMaster);
        }

        PaxosSTM.getInstance().enterBarrier("end", _params.nodesNo);

    }

    private void round(boolean isMaster) throws InterruptedException {

        if (isMaster) {
            new Transaction() {
                @Override
                public void atomic() {
                    TInt tInt = new TInt(0);
                    PaxosSTM.getInstance().addToSharedObjectRegistry("t_int", tInt);
                }
            };
            makeSnapshot();
        }

        PaxosSTM.getInstance().enterBarrier("init", _params.nodesNo);
        makeSnapshot();

        TInt tInt = (TInt) PaxosSTM.getInstance().getFromSharedObjectRegistry("t_int");

        Thread[] threads = new Thread[_params.threadsNo];
        for (int i = 0; i < _params.threadsNo; i++) {
            threads[i] = new Thread(new SimpleWorker(tInt, _params, i));
        }
        for (int i = 0; i < _params.threadsNo; i++) {
            threads[i].start();
        }

        for (int i = 0; i < _params.threadsNo; i++) {
            threads[i].join();
        }

        PaxosSTM.getInstance().enterBarrier("stop", _params.nodesNo);
        new Transaction() {
            @Override
            public void atomic() {
                TInt i = (TInt) PaxosSTM.getInstance().getFromSharedObjectRegistry("t_int");
                System.out.println(i.getInt());
            }
        };
    }
}
