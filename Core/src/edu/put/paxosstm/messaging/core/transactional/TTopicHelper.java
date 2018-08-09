package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.core.data.Message;
import edu.put.paxosstm.messaging.core.data.MessageWithIndex;
import soa.paxosstm.dstm.ArrayWrapper;
import soa.paxosstm.dstm.TransactionObject;

@TransactionObject
public class TTopicHelper {

    private final int bufferSize;
    private int newestIndex = -1;
    private ArrayWrapper<String> tArray;


    public TTopicHelper() {
        this(10000);
    }

    public TTopicHelper(int bufferSize) {
        this.bufferSize = bufferSize;
        String[] internalArray = new String[bufferSize];
        tArray = new ArrayWrapper<>(internalArray);
    }

    public MessageWithIndex get(int i) {
        if(newestIndex == -1 || i > newestIndex) {
            return null;
        }
        int oldest = newestIndex - bufferSize;
        if (i < oldest) {
            return new MessageWithIndex(tArray.get(oldest), oldest);
        } else {
            return  new MessageWithIndex(tArray.get(i), i);
        }
    }

    void add(Message message) {
        newestIndex++;
        tArray.set(newestIndex % bufferSize, message.getData());
    }
}
