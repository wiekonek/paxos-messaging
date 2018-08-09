package benchmark;

import benchmark.config.BasicScenarioParameters;
import benchmark.config.SshLocalEnvironment;
import benchmark.core.ScenarioRunner;
import commands.Command;
import commands.SshCommand;
import commands.paxosstm.Parameters;
import commands.paxosstm.PaxosSTMTestCommand;
import soa.paxosstm.dstm.internal.TransactionOracle;
import tests.paxosstm.EnvironmentConf;
import tools.Tools;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Benchmark {
    static void run(int nodesNo, Class<?> scenarioClass, BasicScenarioParameters[] parameters)
            throws IOException {

        String directoryName = "benchmark-results/" + LocalDateTime.now() + "-" + scenarioClass.getSimpleName();
        runners.BenchmarkRunner.createDirectoryIfNotExists(directoryName);

        EnvironmentConf env = setupEnvironment(nodesNo);
        Command[] commands = new Command[nodesNo];
        int[] timeouts = new int[nodesNo];
        String[] outFilenames = new String[nodesNo];
        String[] errFilenames = new String[nodesNo];

        for (int i = 0; i < nodesNo; i++) {

            String[] encodedParameters = new String[3];
            encodedParameters[0] = TransactionOracle.DeferredUpdateOracle.class.getSimpleName();
            encodedParameters[1] = scenarioClass.getName();
            encodedParameters[2] = Tools.toString(parameters[i]);

            outFilenames[i] = directoryName + "/out" + i + ".txt";
            errFilenames[i] = directoryName + "/out" + i + "-err.txt";
            PaxosSTMTestCommand paxosstmTestCommand = new PaxosSTMTestCommand(
                    ScenarioRunner.class.getName(),
                    encodedParameters,
                    i,
                    env.confString,
                    new String[]{
                            Parameters.WORKSPACE + "/PaxosMessaging/out/production/Benchmark",
                            Parameters.WORKSPACE + "/PaxosMessaging/out/production/Core",
                            Parameters.WORKSPACE + "/BenchmarkRunner/bin"
                    },
                    null
            );
            Command execCommand = new Command(
                    "cd " + Parameters.getPaxosSTMConfFiles() + "; " + paxosstmTestCommand.toString() + ";"
            );
            Command bashCommand = new Command("bash", "-c", execCommand.toString());
            SshCommand sshCommand = new SshCommand(null, env.loginsAtHosts[i],
                    Arrays.asList("bash -ic \"" + execCommand.toString() + "\""));

            commands[i] = env.loginsAtHosts[i] == null || env.loginsAtHosts[i].equals("localhost") ? bashCommand : sshCommand;
            timeouts[i] = 60000;
        }
        for (Command cmd : commands) {
            System.out.println(cmd);
        }
        runners.BenchmarkRunner.runConcurrentCommands(commands, timeouts, outFilenames, errFilenames, 1);
    }

    static void setup() {
        setup(
                "/home/wiekonek/Documents/magisterka-local/paxosstm-all",
                "/home/wiekonek/Documents/magisterka-local/paxosstm-all/PaxosSTM/logback.xml"
        );
    }

    static void setup(String paxosWorkspace, String logbackConfigFilePath) {
        Parameters.WORKSPACE = paxosWorkspace;
        Parameters.SYSTEM_PROPERTIES = "-Dlogback.configurationFile=file://" + logbackConfigFilePath;
        Parameters.JVM_SETTINGS = "-Xmx256m -Xms256m -Xmn128m";
    }

    private static EnvironmentConf setupEnvironment(int nodesNo) {
        EnvironmentConf env = new SshLocalEnvironment("wiekonek", nodesNo);
        runners.Main.killArray = env.loginsAtHosts;
        System.out.println("Config:");
        System.out.println(env.confString);
        System.out.println();
        return env;
    }
}
