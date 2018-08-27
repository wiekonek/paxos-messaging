package benchmark.config;

import runners.BenchmarkRunner;
import tests.paxosstm.EnvironmentConf;

public class LocalEnvironment extends EnvironmentConf {
    public LocalEnvironment(int nodeNo) {
        String[] hosts = new String[nodeNo];

        for (int i = 0; i < nodeNo; i++) {
            hosts[i] = "localhost";
        }

        confString = BenchmarkRunner.getPaxosProperties(
                hosts,
                nodeNo,
                "NIO",
                2,
                true,
                true,
                false
        );
        edur = false;
        pcss = false;
        slurmJobId = null;
        label = null;
    }
}
