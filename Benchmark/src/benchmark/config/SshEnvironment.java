package benchmark.config;

import runners.BenchmarkRunner;

public final class SshEnvironment extends LocalEnvironment {

    public SshEnvironment(String[] hosts) {
        super(hosts.length);

        loginsAtHosts = hosts;
        confString = BenchmarkRunner.getPaxosProperties(
                hosts,
                hosts.length,
                "NIO",
                2,
                true,
                true,
                false
        );
    }
}
