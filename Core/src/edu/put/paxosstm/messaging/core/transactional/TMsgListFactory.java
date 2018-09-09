package edu.put.paxosstm.messaging.core.transactional;

public class TMsgListFactory {
    public static TMsgList create(TMsgListType type) {
        switch (type) {
            case OneEntry:
                return new TMsgListOneEntry();
            case TwoEntry:
                return new TMsgListTwoEntry();
            default:
                System.out.println("Using default TMsgListType: TwoEntry");
                return new TMsgListTwoEntry();
        }
    }
}
