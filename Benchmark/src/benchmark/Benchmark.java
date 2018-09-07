package benchmark;

import benchmark.config.LocalEnvironment;
import benchmark.config.SshEnvironment;
import benchmark.core.ArgumentParsingException;
import commands.Command;
import commands.SshCommand;
import commands.paxosstm.Parameters;
import commands.paxosstm.PaxosSTMTestCommand;
import tests.paxosstm.EnvironmentConf;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

public class Benchmark {
    private final BenchmarkArgumentParser arguments;
    private final int nodesNo;
    private final Class<? extends Scenario> scenarioClass;

    private Benchmark(BenchmarkArgumentParser arguments) throws ArgumentParsingException {
        this.arguments = arguments;
        this.nodesNo = arguments.getNodesNo();
        this.scenarioClass = Scenario.getScenarioClass(arguments.getScenarioType());
        System.out.printf("Running with: [%s]\n", arguments);
        setupPaxos();
    }

    public static void main(String[] args) {
        BenchmarkArgumentParser benchmarkArgs;
        try {
            benchmarkArgs = new BenchmarkArgumentParser(args);
            if(benchmarkArgs.hasHelp()) {
                return;
            }
            new Benchmark(benchmarkArgs).run();
        } catch (ArgumentParsingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {

        String directoryName = "benchmark-results/" +
                LocalDateTime.now() +
                "-" +
                scenarioClass.getSimpleName() +
                (arguments.hasOutputName() ? "-" + arguments.getOutputName() : "");
        runners.BenchmarkRunner.createDirectoryIfNotExists(directoryName);
        System.out.printf("Output directory: %s\n", System.getProperty("user.dir") + "\\"+ directoryName);

        Command[] commands = new Command[nodesNo];
        int[] timeouts = new int[nodesNo];
        String[] outFilenames = new String[nodesNo];
        String[] errFilenames = new String[nodesNo];

        EnvironmentConf environment = setupEnvironment();

        for (int i = 0; i < nodesNo; i++) {

            outFilenames[i] = directoryName + "/out" + i + ".txt";
            errFilenames[i] = directoryName + "/out" + i + "-err.txt";
            PaxosSTMTestCommand paxosstmTestCommand = new PaxosSTMTestCommand(
                    ScenarioRunner.class.getName(),
                    arguments.getArgs(),
                    i,
                    environment.confString,
                    new String[]{
                            Parameters.WORKSPACE + "/PaxosMessaging/out/production/Benchmark",
                            Parameters.WORKSPACE + "/PaxosMessaging/out/production/Core",
                            Parameters.WORKSPACE + "/BenchmarkRunner/bin",
                            Parameters.WORKSPACE + "/PaxosMessaging/Benchmark/lib/jopt-simple-5.0.4.jar"
                    },
                    null
            );

            Command execCommand = new Command(
                    "cd " + Parameters.getPaxosSTMConfFiles() + "; " + paxosstmTestCommand.toString() + ";"
            );

            if (arguments.isSsh()) {
                commands[i] = new SshCommand(
                        null,
                        environment.loginsAtHosts[i],
                        Collections.singletonList("bash -ic \"" + execCommand.toString() + "\"")
                );
            } else {
                commands[i] = new Command("bash", "-c", execCommand.toString());
            }

            timeouts[i] = arguments.getTimeout();
        }
        System.out.println("Start benchmarking");
        for (Command cmd: commands) {
            System.out.println(cmd.toString());
        }
        runners.BenchmarkRunner.runConcurrentCommands(commands, timeouts, outFilenames, errFilenames, 1);
        System.out.println("Finished");
    }

    private void setupPaxos() {
        if (arguments.hasWorkspace()) {
            Parameters.WORKSPACE = arguments.getWorkspace();
        } else {
            String workingDir = System.getProperty("user.dir");
            workingDir = workingDir.substring(0, workingDir.lastIndexOf('/')); // parent
            System.out.println("Assuming current location as child of paxos workspace: " + workingDir);
            Parameters.WORKSPACE = workingDir;
        }
        Parameters.SYSTEM_PROPERTIES =
                "-Dlogback.configurationFile=file://" + Parameters.WORKSPACE + "/PaxosSTM/logback.xml";
        Parameters.JVM_SETTINGS = "-Xmx256m -Xms256m -Xmn128m";
    }


    private EnvironmentConf setupEnvironment() {
        EnvironmentConf environment;
        if (arguments.isSsh()) {
            environment = new SshEnvironment(arguments.sshHosts());
            runners.Main.killArray = environment.loginsAtHosts;
        } else {
            environment = new LocalEnvironment(nodesNo);
        }
        return environment;
    }
}
