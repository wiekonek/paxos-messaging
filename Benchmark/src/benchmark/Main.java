package benchmark;

import commands.Command;
import commands.SshCommand;
import commands.paxosstm.Parameters;
import commands.paxosstm.PaxosSTMTestCommand;
import benchmark.config.SshLocalEnvironment;
import benchmark.scenarios.ScenarioRunner;
import benchmark.config.BasicScenarioParameters;
import benchmark.scenarios.MessagingSystemScenario;
import runners.BenchmarkRunner;
import soa.paxosstm.dstm.internal.TransactionOracle;
import tests.paxosstm.EnvironmentConf;
import tools.Tools;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: [nodesNo]");
            return;
        }
        System.out.printf("Running with: [nodesNo=%s]\n", args[0]);
        int nodesNo = Integer.parseInt(args[0]);


        Parameters.WORKSPACE = "/home/wiekonek/Documents/magisterka-local/paxosstm-all";
        Parameters.SYSTEM_PROPERTIES = "-Dlogback.configurationFile=file:///home/wiekonek/Documents/magisterka-local/paxosstm-all/PaxosSTM/logback.xml";
        Parameters.JVM_SETTINGS = "-Xmx256m -Xms256m -Xmn128m";



        EnvironmentConf env = new SshLocalEnvironment("wiekonek", nodesNo);
        runners.Main.killArray = env.loginsAtHosts;
        System.out.println("Config:");
        System.out.println(env.confString);



        String scenarioClassName = MessagingSystemScenario.class.getSimpleName();
        String directoryName = "benchmark-results/" + LocalDateTime.now() + "-" + scenarioClassName;
        BenchmarkRunner.createDirectoryIfNotExists(directoryName);

        Command[] commands = new Command[nodesNo];
        int[] timeouts = new int[nodesNo];
        String[] outFilenames = new String[nodesNo];
        String[] errFilenames = new String[nodesNo];


//        SimpleScenarioParameters[] parameters = new SimpleScenarioParameters[nodesNo];
//        for (int i = 0; i < nodesNo; i++) {
//            parameters[i] = new SimpleScenarioParameters(10, threadsPerNode, nodesNo, i);
//        }

        BasicScenarioParameters[] parameters = new BasicScenarioParameters[nodesNo];
        for (int i = 0; i < nodesNo; i++) {
            parameters[i] = new BasicScenarioParameters(nodesNo, 3);
        }



        for (int i = 0; i < nodesNo; i++) {

            String[] encodedParameters = new String[3];
            encodedParameters[0] = TransactionOracle.DeferredUpdateOracle.class.getSimpleName();
            encodedParameters[1] = scenarioClassName;
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
            timeouts[i] = 20000;
        }
        for (Command cmd : commands) {
            System.out.println(cmd);
        }
        BenchmarkRunner.runConcurrentCommands(commands, timeouts, outFilenames, errFilenames, 1);
    }
}

