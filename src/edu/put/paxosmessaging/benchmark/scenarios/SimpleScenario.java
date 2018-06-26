package edu.put.paxosmessaging.benchmark.scenarios;

import edu.put.paxosmessaging.benchmark.config.SimpleScenarioParameters;
import edu.put.paxosmessaging.core.TInt;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.Transaction;
import tools.Tools;


public class SimpleScenario extends Scenario {
    private final SimpleScenarioParameters[] _paramArray;

    public SimpleScenario(String[] params) {
        super(params);
        _paramArray = new SimpleScenarioParameters[params.length];
        for (int i = 0; i < params.length; i++) {
            SimpleScenarioParameters p = new SimpleScenarioParameters();
            try {
                Tools.fromString(params[i], p);
            } catch (Exception e) {
                e.printStackTrace();
            }
            _paramArray[i] = p;
        }
    }

    @Override
    protected void runBenchmark(boolean isMaster) {

        // TODO: Get threadsNo for specific replica?? Temporary use first params in array
        SimpleScenarioParameters params = _paramArray[0];

        for (int i = 0; i < 2; i++) {
            round(isMaster, params);
        }

        PaxosSTM.getInstance().enterBarrier("end", params.nodesNo);

    }

    private void round(boolean isMaster, SimpleScenarioParameters params) {
        if(isMaster) {
            new Transaction() {
                @Override
                public void atomic() {
                    TInt tInt = new TInt(0);
                    PaxosSTM.getInstance().addToSharedObjectRegistry("t_int", tInt);
                }
            };
            makeSnapshot();
        }

        PaxosSTM.getInstance().enterBarrier("init", params.nodesNo);
        makeSnapshot();

        TInt tInt = (TInt) PaxosSTM.getInstance().getFromSharedObjectRegistry("t_int");

        Thread[] threads = new Thread[params.threadsNo];
        for (int i = 0; i < params.threadsNo; i++) {
            threads[i] = new Thread(new SimpleWorkerRunnable(tInt, params, i));
        }
        for (int i = 0; i < params.threadsNo; i++) {
            threads[i].start();
        }

        PaxosSTM.getInstance().enterBarrier("stop", params.nodesNo);
    }
}
