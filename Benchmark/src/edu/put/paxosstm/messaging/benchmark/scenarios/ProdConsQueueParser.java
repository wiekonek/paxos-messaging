package edu.put.paxosstm.messaging.benchmark.scenarios;

import edu.put.paxosstm.messaging.queue.QueueSelectionStrategy;
import edu.put.paxosstm.messaging.core.transactional.TMsgListType;
import joptsimple.ArgumentAcceptingOptionSpec;

import static java.util.Arrays.asList;

public class ProdConsQueueParser extends ProdConsParser {

    private ArgumentAcceptingOptionSpec<Integer> concurrentQueuesOption;
    private ArgumentAcceptingOptionSpec<QueueSelectionStrategy> selectionStrategyOption;
    private ArgumentAcceptingOptionSpec<Integer> retryNumberOption;
    private ArgumentAcceptingOptionSpec<Integer> retryDelayOption;
    private ArgumentAcceptingOptionSpec<TMsgListType> msgListTypeOption;

    ProdConsQueueParser(String[] args) {
        super(args);
    }

    @Override
    protected void setup() {
        super.setup();

        concurrentQueuesOption = optionParser
                .acceptsAll(asList("q", "concurrent-queues"), "Number of concurrent queues")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(6);

        selectionStrategyOption = optionParser
                .acceptsAll(asList("ss", "selection-strategy"), "Consumer selection strategy")
                .withRequiredArg()
                .ofType(QueueSelectionStrategy.class)
                .defaultsTo(QueueSelectionStrategy.RoundRobin);
        retryNumberOption = optionParser
                .acceptsAll(asList("r", "retryNumber"), "Max number of retries for consumers")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(10);
        retryDelayOption = optionParser
                .acceptsAll(asList("d", "retryDelay"), "Delay for each consumer retry")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(100);
        msgListTypeOption = optionParser
                .acceptsAll(asList("mlt", "msgListType"), "Type of transactional supporting message list")
                .withRequiredArg()
                .ofType(TMsgListType.class)
                .defaultsTo(TMsgListType.OneEntry);

    }

    int getConcurrentQueueNo() {
        return arguments.valueOf(concurrentQueuesOption);
    }

    QueueSelectionStrategy getSelectionStrategy() {
        return arguments.valueOf(selectionStrategyOption);
    }

    int getRetryNumber() {
        return arguments.valueOf(retryNumberOption);
    }

    int getRetryDelay() {
        return arguments.valueOf(retryDelayOption);
    }

    TMsgListType getMsgListType() {
        return arguments.valueOf(msgListTypeOption);
    }

}
