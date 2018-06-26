package edu.put.paxosmessaging.benchmark.config;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TIntParameters implements Externalizable {
    public int requestsNo;

    public TIntParameters(int requestsNo) {
        this.requestsNo = requestsNo;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(requestsNo);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        requestsNo = in.readInt();
    }
}
