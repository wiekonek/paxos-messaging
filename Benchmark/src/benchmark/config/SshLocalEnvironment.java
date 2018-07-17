package benchmark.config;

import commands.paxosstm.Parameters;
import runners.BenchmarkRunner;
import tests.paxosstm.EnvironmentConf;

public final class SshLocalEnvironment extends EnvironmentConf {
    public SshLocalEnvironment(String login, int numberOfProcesses) {

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
