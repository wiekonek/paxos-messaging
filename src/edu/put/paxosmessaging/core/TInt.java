package edu.put.paxosmessaging.core;


import soa.paxosstm.dstm.TransactionObject;

@TransactionObject
public class TInt {
    private int _int;

    public TInt(int value) {
        _int = value;
    }


    public int getInt() {
        return _int;
    }

    public void setInt(int value) {
        _int = value;
    }
}
