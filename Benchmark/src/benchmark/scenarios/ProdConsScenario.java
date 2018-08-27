package benchmark.scenarios;

import benchmark.Scenario;
import benchmark.scenarios.workers.SimpleQueueConsumer;
import benchmark.scenarios.workers.SimpleQueueProducer;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.MQueueParams;
import edu.put.paxosstm.messaging.core.MessageQueue;
import edu.put.paxosstm.messaging.core.queue.MQueueType;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.asList;


public class ProdConsScenario extends Scenario {
    private final ProdConsParser argsParser;

    public ProdConsScenario(int roundsNo, String[] args) {
        super(roundsNo, args);
        argsParser = new ProdConsParser(args);
        System.out.println(argsParser);
    }

    protected void round() throws MessagingException {

        MessageQueue queue = messagingContext.createQueueWithStatisticsCollection(
                "messages-queue",
                new MQueueParams(MQueueType.Multi, argsParser.getConcurrentQueueNo())
        );
        barrier("init-round");

        int producersNo = argsParser.getProducersNo();
        Thread[] producers = new Thread[producersNo];
        for (int i = 0; i < producersNo; i++) {
            producers[i] = new Thread(new SimpleQueueProducer(messagingContext, queue, i, argsParser.getProductsNo()));
            producers[i].start();
        }

        barrier("init-producers");

        int consumersNo = argsParser.getConsumersNo();
        Thread[] consumers = new Thread[consumersNo];
        for (int i = 0; i < consumersNo; i++) {
            consumers[i] = new Thread(new SimpleQueueConsumer(queue, i));
            consumers[i].start();
        }

        try {
            for (Thread t : consumers) t.join();
            for (Thread t : producers) t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print(queue.getCollectedStatistics().getStatisticsLog());
        barrier("stop-round");
    }

    static class ProdConsParser {
        private final OptionParser optionParser;
        private final OptionSet arguments;

        private final ArgumentAcceptingOptionSpec<Integer> producersNoOption;
        private final ArgumentAcceptingOptionSpec<Integer> productsNoOption;
        private final ArgumentAcceptingOptionSpec<Integer> consumersNoOption;
        private final ArgumentAcceptingOptionSpec<Integer> concurrentQueuesOption;
        private final String[] args;

        ProdConsParser(String[] args) {
            this.args = args;
            optionParser = new OptionParser();

            concurrentQueuesOption = optionParser
                    .acceptsAll(asList("q", "concurrent-queues"), "Number of concurrent queues")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(6);


            producersNoOption = optionParser
                    .acceptsAll(asList("p", "producers"), "Number of producers threads for each node")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(2);

            productsNoOption = optionParser
                    .acceptsAll(
                            asList("products", "producers-product"),
                            "Number of products for each producer thread")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(10);

            consumersNoOption = optionParser
                    .acceptsAll(asList("c", "consumers"), "Number of consumers threads for each node")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(2);


            arguments = optionParser.parse(args);
        }

        @Override
        public String toString() {
            return String.join(" ", args);
        }

        int getConcurrentQueueNo() {
            return arguments.valueOf(concurrentQueuesOption);
        }

        int getProducersNo() {
            return arguments.valueOf(producersNoOption);
        }

        int getProductsNo() {
            return arguments.valueOf(productsNoOption);
        }

        int getConsumersNo() {
            return arguments.valueOf(consumersNoOption);
        }

    }

}
