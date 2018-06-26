package edu.put.paxosmessaging.benchmark;

import commands.Command;
import commands.SshCommand;
import commands.paxosstm.Parameters;
import commands.paxosstm.PaxosSTMTestCommand;
import edu.put.paxosmessaging.benchmark.config.*;
import tests.paxosstm.EnvironmentConf;
import runners.BenchmarkRunner;
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

        EnvironmentConf env = new SshLocalEnvironment("wiekonek", nodesNo);
        System.out.println("Config:");
        System.out.println(env.confString);



//        Command[] commands;
//        commands = new Command[]{
//                new Command("pwd"),
//                new Command("ls", "-la")
//        };
//
//        String directoryName = "scenario-out-test-benchmark";
//        BenchmarkRunner.createDirectoryIfNotExists(directoryName);
//
//        int[] timeouts = new int[nodesNo];
//        String[] errFile =  new String[nodesNo];
//        String[] outFile = new String[nodesNo];
//
//        for (int i = 0; i < nodesNo; i++) {
//            errFile[i] = directoryName + "/out" + i + ".err.txt";
//            outFile[i] = directoryName + "/out" + i + ".txt";
//            timeouts[i] = 10000;
//        }
//
//        System.out.println(commands[0].getCommand());

        String[] loginsAtHosts = env.loginsAtHosts;


        String directoryName = "scenario-out-01";
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

        // TODO: ??>
        encodedParameters[0] = "DeferredUpdateOracle";

        for (int i = 0; i < parameters.length; i++)
            encodedParameters[i + 1] = Tools.toString(parameters[i]);

        for (int i = 0; i < loginsAtHosts.length; i++) {
            int replicaId = loginsAtHosts[i] != null ? i : -1;


            parameters[0].replicaId = replicaId;

            outFilenames[i] = directoryName + "/" + filename;
            errFilenames[i] = directoryName + "/" + filename + "-err";

            PaxosSTMTestCommand paxosstmTestCommand = new PaxosSTMTestCommand(
                    "edu.put.paxosmessaging.benchmark.scenarios.ScenarioRunner", encodedParameters, replicaId, env.confString);
            Command execCommand = new Command(
                    "cd " + Parameters.getPaxosSTMConfFiles() + "; " + paxosstmTestCommand.toString() + ";"
            );
            Command bashCommand = new Command("bash", "-c", execCommand.toString());
            SshCommand sshCommand = new SshCommand(null, loginsAtHosts[i],
                    Arrays.asList("bash -ic \"" + execCommand.toString() + "\""));

            commands[i] = loginsAtHosts[i] == null || loginsAtHosts[i].equals("localhost") ? bashCommand : sshCommand;
            timeouts[i] = 1000;
        }

        System.out.println(commands[0].getCommand());



        BenchmarkRunner.runConcurrentCommands(commands, timeouts, outFilenames, errFilenames, 1);
    }
}

