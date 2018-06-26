package edu.put.paxosmessaging.benchmark.config;

import commands.paxosstm.Parameters;
import runners.BenchmarkRunner;
import tests.paxosstm.EnvironmentConf;

public final class SshLocalEnvironment extends EnvironmentConf {
    public SshLocalEnvironment(String login, int numberOfProcesses) {
        Parameters.WORKSPACE = "/home/wiekonek/Documents/magisterka-local/paxosstm-all";
        Parameters.SYSTEM_PROPERTIES = "-Dlogback.configurationFile=file:///home/wiekonek/Documents/magisterka-local/paxosstm-all/PaxosSTM/logback.xml";
        Parameters.JVM_SETTINGS = "-Xmx256m -Xms256m -Xmn128m";

        String[] hosts = new String[numberOfProcesses];
        for (int i = 0; i < numberOfProcesses; i++) {
            hosts[i] = "localhost";
        }

        confString = BenchmarkRunner.getPaxosProperties(
                hosts,
                numberOfProcesses,
                "NIO",
                2,
                true,
                true,
                false
        );

        loginsAtHosts = new String[numberOfProcesses];
        for (int i = 0; i < numberOfProcesses; i++) {
            loginsAtHosts[i] = login + "@localhost";
        }

        edur = false;
        pcss = false;
        slurmJobId = null;
        label = null;
    }
}
