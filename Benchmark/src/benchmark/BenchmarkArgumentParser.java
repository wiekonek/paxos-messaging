package benchmark;

import benchmark.core.ArgumentParsingException;
import benchmark.core.AvailableScenarios;
import benchmark.core.LogType;
import joptsimple.*;

import java.io.IOException;
import java.util.List;

import static  java.util.Arrays.asList;

@SuppressWarnings("FieldCanBeLocal")
class BenchmarkArgumentParser {

    private final OptionSet arguments;
    private final String[] args;

    private final AbstractOptionSpec<Void> helpOption;

    private final ArgumentAcceptingOptionSpec<Integer> nodesNoOption;
    private final ArgumentAcceptingOptionSpec<Integer> roundsNoOption;
    private final ArgumentAcceptingOptionSpec<String> sshHostOption;
    private final OptionSpecBuilder sshOption;
    private final ArgumentAcceptingOptionSpec<AvailableScenarios> scenarioOption;
    private final ArgumentAcceptingOptionSpec<Integer> timeoutOption;
    private final ArgumentAcceptingOptionSpec<String> outputFolderOption;
    private final ArgumentAcceptingOptionSpec<String> workspaceOption;
    private final ArgumentAcceptingOptionSpec<LogType> logTypeOption;

    int getNodesNo() {
        return arguments.valueOf(nodesNoOption);
    }

    AvailableScenarios getScenarioType()  {
        return arguments.valueOf(scenarioOption);
    }

    int getRoundsNo() {
        return  arguments.valueOf(roundsNoOption);
    }

    int getTimeout() {
        return arguments.valueOf(timeoutOption);
    }

    boolean hasOutputName() {
        return arguments.has(outputFolderOption);
    }

    String getOutputName() {
        return  arguments.valueOf(outputFolderOption);
    }

    boolean hasWorkspace() {
        return arguments.has(workspaceOption);
    }

    String getWorkspace() {
        return arguments.valueOf(workspaceOption);
    }

    LogType getLogType() {
        return arguments.valueOf(logTypeOption);
    }

    String[] getScenarioArgs() {
        //noinspection SuspiciousToArrayCall
        return arguments.nonOptionArguments().toArray(new String[0]);
    }

    String[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return String.join(" ", getArgs());
    }

    boolean isSsh() {
        return arguments.has(sshOption) && arguments.has(sshHostOption);
    }

    String[] sshHosts() {
        return arguments.valuesOf(sshHostOption).toArray(new String[0]);
    }

    BenchmarkArgumentParser(String[] args) throws IOException, ArgumentParsingException {
        this.args = args;

        OptionParser optionParser = new OptionParser();

        helpOption = optionParser
                .accepts("help", "Show help message")
                .forHelp();

        nodesNoOption= optionParser
                .accepts("nodes", "Number of nodes, must be smaller or equal to --ssh-host number")
                .withRequiredArg()
                .ofType(Integer.class)
                .required();

        sshOption =  optionParser
                .accepts("ssh", "Use ssh commands for benchmark");


        sshHostOption = optionParser
                .accepts("ssh-hosts", "Hosts list for ssh option")
                .requiredIf(sshOption)
                .withRequiredArg()
                .describedAs("user1@host1,user2@host2,...")
                .ofType(String.class)
                .withValuesSeparatedBy(',');

        scenarioOption = optionParser
                .acceptsAll(asList("s", "scenario"), "Benchmark scenario to run")
                .withRequiredArg()
                .ofType(AvailableScenarios.class)
                .defaultsTo(AvailableScenarios.SimpleQueue);

        roundsNoOption = optionParser
                .acceptsAll(asList("r", "rounds"), "Number of benchmark rounds to perform")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(3);

        timeoutOption = optionParser
                .acceptsAll(asList("t", "timeout"), "Timeout in milliseconds for nodes")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(60000);

        outputFolderOption = optionParser
                .acceptsAll(asList("o", "outputFolder"), "Additional suffix of output folder")
                .withRequiredArg()
                .ofType(String.class);

        workspaceOption = optionParser
                .acceptsAll(
                        asList("w", "workspace", "path"),
                        "Workscpace path (pointing to folder containing all necessary paxos projects"
                )
                .withRequiredArg()
                .ofType(String.class);

        logTypeOption = optionParser
                .acceptsAll(
                        asList("l", "logType"),
                        "Type of logging"
                )
                .withRequiredArg()
                .ofType(LogType.class)
                .defaultsTo(LogType.Verbose);


        try {
            arguments = optionParser.parse(args);
        } catch (OptionException | NullPointerException e) {
            System.err.println(e.getMessage());
            System.err.println();
            optionParser.printHelpOn(System.err);
            throw new ArgumentParsingException();
        }

        if(arguments.has(helpOption)) {
            optionParser.printHelpOn(System.out);
            return;
        }

        if (arguments.has("ssh")) {
            int nodesNo = arguments.valueOf(nodesNoOption);
            List<String> hosts = arguments.valuesOf(sshHostOption);
            if (nodesNo > hosts.size()) {
                System.err.printf("Not enough hosts [%s] for %d nodes\n", String.join(",", hosts), nodesNo);
                throw new ArgumentParsingException();
            }
        }

    }
}
