package benchmark.scenarios;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.asList;

public class ProdConsParser {
    final OptionParser optionParser;
    final OptionSet arguments;

    private ArgumentAcceptingOptionSpec<Integer> producersNoOption;
    private ArgumentAcceptingOptionSpec<Integer> productsNoOption;
    private ArgumentAcceptingOptionSpec<Integer> consumersNoOption;
    private String[] args;

    ProdConsParser(String[] args) {
        this.args = args;
        optionParser = new OptionParser();
        setup();
        arguments = optionParser.parse(args);
    }

    protected void setup() {
        producersNoOption = optionParser
                .acceptsAll(asList("p", "producers"), "Number of producers threads for each node")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(2);

        productsNoOption = optionParser
                .acceptsAll(
                        asList("pp", "products", "producer-products"),
                        "Number of products for each producer thread")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(10);

        consumersNoOption = optionParser
                .acceptsAll(asList("c", "s", "consumers", "subscribers"), "Number of consumers/subscribers threads for each node")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(2);
    }

    @Override
    public String toString() {
        return String.join(" ", args);
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