package edu.put.paxosstm.messaging.core.transactional;

import edu.put.paxosstm.messaging.data.Message;
import edu.put.paxosstm.messaging.data.MessageWithIndex;
import soa.paxosstm.dstm.ArrayWrapper;
import soa.paxosstm.dstm.TransactionObject;

@TransactionObject
public class TTopicHelper {

    private final int bufferSize;
    private int newestIndex = -1;
    private ArrayWrapper<String> tArray;


    public TTopicHelper() {
        this(1000);
    }

    public TTopicHelper(int bufferSize) {
        this.bufferSize = bufferSize;
        String[] internalArray = new String[bufferSize];
        tArray = new ArrayWrapper<>(internalArray);
    }

    public MessageWithIndex getNewest() {
        if (newestIndex == -1) return null;
        return new MessageWithIndex(
                tArray.get(newestIndex % bufferSize),
                newestIndex
        );
    }

    public MessageWithIndex get(int i) {
        if (newestIndex == -1 || i > newestIndex) {
            return null;
        }
        int oldest = newestIndex - bufferSize + 1;
        if (i < oldest) {
            return new MessageWithIndex(tArray.get(oldest % bufferSize), oldest);
        } else {
            return new MessageWithIndex(tArray.get(i % bufferSize), i);
        }
    }

    public void add(Message message) {
        newestIndex++;
        tArray.set(newestIndex % bufferSize, message.getData());
    }

    public void clean() {
        String[] internalArray = new String[bufferSize];
        tArray = new ArrayWrapper<>(internalArray);
        newestIndex = -1;
    }
}
