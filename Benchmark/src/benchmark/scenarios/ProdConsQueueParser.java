package benchmark.scenarios;

import edu.put.paxosstm.messaging.core.queue.QueueSelectionStrategy;
import joptsimple.ArgumentAcceptingOptionSpec;

import static java.util.Arrays.asList;

public class ProdConsQueueParser extends ProdConsParser {

    private ArgumentAcceptingOptionSpec<Integer> concurrentQueuesOption;
    private ArgumentAcceptingOptionSpec<QueueSelectionStrategy> selectionStrategyOption;

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
    }

    int getConcurrentQueueNo() {
        return arguments.valueOf(concurrentQueuesOption);
    }

    QueueSelectionStrategy getSelectionStrategy() {
        return arguments.valueOf(selectionStrategyOption);
    }
}
