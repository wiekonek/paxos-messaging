package edu.put.paxosmessaging.benchmark;

import commands.Command;
import edu.put.paxosmessaging.benchmark.config.*;
import tests.paxosstm.EnvironmentConf;
import runners.BenchmarkRunner;



public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: [processesNo threadsPerProcess]");
            return;
        }
        System.out.printf("Running with: [processesNo=%s threadsPerProcess=%s]\n", args[0], args[1]);
        int processesNo = Integer.parseInt(args[0]);
        int threadsPerProcess = Integer.parseInt(args[1]);

        EnvironmentConf env = new SshLocalEnvironment("wiekonek", processesNo);
        System.out.println("Config:");
        System.out.println(env.confString);

        Command[] commands;
        commands = new Command[]{
                new Command("pwd"),
                new Command("ls", "-la")
        };

        String directoryName = "scenario-out-test-benchmark";
        BenchmarkRunner.createDirectoryIfNotExists(directoryName);

        int[] timeouts = new int[processesNo];
        String[] errFile =  new String[processesNo];
        String[] outFile = new String[processesNo];

        for (int i = 0; i < processesNo; i++) {
            errFile[i] = directoryName + "/out" + i + ".err.txt";
            outFile[i] = directoryName + "/out" + i + ".txt";
            timeouts[i] = 10000;
        }

        System.out.println(commands[0].getCommand());

        BenchmarkRunner.runConcurrentCommands(commands, timeouts, outFile, errFile, 1);
    }
}

