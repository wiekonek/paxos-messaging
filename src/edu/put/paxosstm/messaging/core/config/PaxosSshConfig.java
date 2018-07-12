package edu.put.paxosstm.messaging.core.config;

public class PaxosSshConfig {
    public static class Host {

        private final String name;
        public String getName() {
            return name;
        }

        private final String login;
        public String getLogin() {
            return login;
        }

        public Host(String name, String login) {
            this.name = name;
            this.login = login;
        }
    }

    private Host[] hosts;
    public Host[] getHosts() {
        return hosts;
    }

    private int nodeNo;
    public int getNodeNo() {
        return nodeNo;
    }

    public PaxosSshConfig(Host[] hosts) {
        this.hosts = hosts;
        this.nodeNo = hosts.length;
    }

    public PaxosSshConfig withHost(Host host) {
        Host[] newHosts = new Host[hosts.length + 1];
        for (int i = 0; i < hosts.length; i++) {
            newHosts[i] = hosts[i];
        }
        newHosts[hosts.length] = host;
        return new PaxosSshConfig(newHosts);
    }
}
