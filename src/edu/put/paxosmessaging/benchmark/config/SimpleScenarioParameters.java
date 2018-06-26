package edu.put.paxosmessaging.benchmark.config;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SimpleScenarioParameters implements Externalizable {
    public int requestsNo;
    public int threadsNo;
    public int nodesNo;

    public SimpleScenarioParameters(int requestsNo, int threadsNo, int nodesNo) {
        this.requestsNo = requestsNo;
        this.threadsNo = threadsNo;
        this.nodesNo = nodesNo;
    }

    public SimpleScenarioParameters() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(requestsNo);
        out.writeInt(threadsNo);
        out.writeInt(nodesNo);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        requestsNo = in.readInt();
        threadsNo = in.readInt();
        nodesNo = in.readInt();
    }
}
