package edu.put.paxosstm.messaging.core.data;

import java.io.Serializable;

public class Message implements Serializable {
    private String data;

    public Message(String data) {
        this.data = data;
    }

    public Message(int intData) {
        this.data = Integer.toString(intData);
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return data;
    }
}
