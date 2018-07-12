package edu.put.paxosstm.messaging.benchmark.config;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BasicScenarioParameters implements Externalizable {

    public int nodesNumber;
    public int roundsNumber;

    public BasicScenarioParameters() {
    }

    public BasicScenarioParameters(int nodesNumber, int roundsNumber) {
        this.roundsNumber = roundsNumber;
        this.nodesNumber = nodesNumber;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(nodesNumber);
        out.writeInt(roundsNumber);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        nodesNumber = in.readInt();
        roundsNumber = in.readInt();
    }
}
