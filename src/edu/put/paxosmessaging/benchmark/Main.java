package edu.put.paxosmessaging.benchmark;

import commands.Command;
import commands.SshCommand;
import commands.paxosstm.Parameters;
import commands.paxosstm.PaxosSTMTestCommand;
import edu.put.paxosmessaging.benchmark.config.SimpleScenarioParameters;
import edu.put.paxosmessaging.benchmark.config.SshLocalEnvironment;
import edu.put.paxosmessaging.benchmark.scenarios.ScenarioRunner;
import runners.BenchmarkRunner;
import tests.paxosstm.EnvironmentConf;
import tools.Tools;

import java.io.IOException;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: [nodesNo threadsPerNode]");
            return;
        }
        System.out.printf("Running with: [nodesNo=%s threadsPerNode=%s]\n", args[0], args[1]);
        int nodesNo = Integer.parseInt(args[0]);
        int threadsPerNode = Integer.parseInt(args[1]);

        Parameters.WORKSPACE = "/home/wiekonek/Documents/magisterka-local/paxosstm-all";
        Parameters.SYSTEM_PROPERTIES = "-Dlogback.configurationFile=file:///home/wiekonek/Documents/magisterka-local/paxosstm-all/PaxosSTM/logback.xml";
        Parameters.JVM_SETTINGS = "-Xmx256m -Xms256m -Xmn128m";

        EnvironmentConf env = new SshLocalEnvironment("wiekonek", nodesNo);
        System.out.println("Config:");
        System.out.println(env.confString);

        String[] loginsAtHosts = env.loginsAtHosts;

        String directoryName = "benchmark-results/scenario-out-queue-01";
        String filename = "out";

        BenchmarkRunner.createDirectoryIfNotExists(directoryName);

        Command[] commands = new Command[loginsAtHosts.length];
        int[] timeouts = new int[loginsAtHosts.length];
        String[] outFilenames = new String[loginsAtHosts.length];
        String[] errFilenames = new String[loginsAtHosts.length];


        SimpleScenarioParameters[] parameters = new SimpleScenarioParameters[nodesNo];
        for (int i = 0; i < nodesNo; i++) {
            parameters[i] = new SimpleScenarioParameters(10, threadsPerNode, nodesNo, i);
        }


        String[] encodedParameters = new String[parameters.length + 1];

        encodedParameters[0] = "DeferredUpdateOracle";

        for (int i = 0; i < parameters.length; i++)
            encodedParameters[i + 1] = Tools.toString(parameters[i]);

        for (int i = 0; i < loginsAtHosts.length; i++) {
            int replicaId = loginsAtHosts[i] != null ? i : -1;


            parameters[0].replicaId = replicaId;

            outFilenames[i] = directoryName + "/" + filename + i + ".txt";
            errFilenames[i] = directoryName + "/" + filename + i + "-err.txt";
            PaxosSTMTestCommand paxosstmTestCommand = new PaxosSTMTestCommand(
                    ScenarioRunner.class.getName(),
                    encodedParameters,
                    replicaId,
                    env.confString,
                    new String[]{
                            Parameters.WORKSPACE + "/PaxosMessaging/bin/production/PaxosMessaging",
                            Parameters.WORKSPACE + "/BenchmarkRunner/bin"
                    },
                    null
            );
            Command execCommand = new Command(
                    "cd " + Parameters.getPaxosSTMConfFiles() + "; " + paxosstmTestCommand.toString() + ";"
            );
            Command bashCommand = new Command("bash", "-c", execCommand.toString());
            SshCommand sshCommand = new SshCommand(null, loginsAtHosts[i],
                    Arrays.asList("bash -ic \"" + execCommand.toString() + "\""));

            commands[i] = loginsAtHosts[i] == null || loginsAtHosts[i].equals("localhost") ? bashCommand : sshCommand;
            timeouts[i] = 20000;
        }

        System.out.println(commands[0].getCommand());


        BenchmarkRunner.runConcurrentCommands(commands, timeouts, outFilenames, errFilenames, 1);
    }
}

