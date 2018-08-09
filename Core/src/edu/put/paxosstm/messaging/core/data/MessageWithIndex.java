package edu.put.paxosstm.messaging.core.data;

public class MessageWithIndex extends Message {
    private final int index;

    public MessageWithIndex(String data, int index) {
        super(data);
        this.index = index;
    }

    public MessageWithIndex(int intData, int index) {
        super(intData);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
