package edu.put.paxosmessaging.benchmark.scenarios;

import edu.put.paxosmessaging.benchmark.config.SimpleScenarioParameters;
import edu.put.paxosmessaging.core.transactional.TInt;
import soa.paxosstm.dstm.Transaction;

public class SimpleWorker implements Runnable {

    private final TInt _int;
    private final SimpleScenarioParameters _params;
    private final int _processId;

    public SimpleWorker(TInt value, SimpleScenarioParameters params, int processId) {
        _int = value;
        _params = params;
        _processId = processId;
    }

    @Override
    public void run() {
        for (int i = 0; i < 2; i++) {
            new Transaction() {
                @Override
                public void atomic() {
                    int val = _int.getInt();
                    _int.setInt(val + 2);
                }
            };
        }
    }
}
