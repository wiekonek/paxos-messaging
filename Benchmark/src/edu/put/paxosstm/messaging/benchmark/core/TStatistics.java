package edu.put.paxosstm.messaging.benchmark.core;

import edu.put.paxosstm.messaging.benchmark.RoundStatistics;
import soa.paxosstm.dstm.TransactionObject;

@TransactionObject
public class TStatistics {

    @TransactionObject
    public static class Box {
        RoundStatistics statistics = new RoundStatistics();

        public void set(RoundStatistics value) {
            statistics = value;
        }

        public RoundStatistics get() {
            return statistics;
        }
    }

    private Box box = new Box();

    public TStatistics() {
    }

    public TStatistics(RoundStatistics value) {
        super();
        box.set(value);
    }

    public void add(RoundStatistics value) {
        box.set(box.get().add(value));
    }

    public RoundStatistics get() {
        return box.get();
    }

    public void clear() {
        box.set(new RoundStatistics());
    }
}
