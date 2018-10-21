package edu.put.paxosstm.messaging;

import java.util.Arrays;

/**
 * Configuration of PaxosMessaging.
 */
public class MessagingEnvironmentConfig {
    public static class Node {

        private final int id;
        private final String hostname;
        private final int clientPort;
        private final int replicaPort;

        public Node(int id, String hostname, int clientPort, int replicaPort) {
            this.id = id;
            this.hostname = hostname;
            this.clientPort = clientPort;
            this.replicaPort = replicaPort;
        }

        int getId() {
            return id;
        }

        @Override
        public String toString() {
            return String.format("process.%d = %s:%d:%d\n",id, hostname, clientPort, replicaPort);
        }
    }

    final int nodeId;
    final Node[] nodes;

    /**
     * Create configuration for specific node.
     * @param nodeId Id of node (must be in nodes).
     */
    public MessagingEnvironmentConfig(int nodeId) {
        this(nodeId, null);
    }

    /**
     * Create configuration for specific node with list of nodes.
     * @param nodeId Id of node (must be in nodes).
     * @param nodes
     */
    private MessagingEnvironmentConfig(int nodeId, Node[] nodes) {
        this.nodeId = nodeId;
        if(nodes == null) {
            this.nodes = new Node[0];
        } else {
            this.nodes = nodes;
        }
    }

    /**
     * Create new {@link MessagingEnvironmentConfig} and add specified node.
     * This method may be chained to create config with multiple nodes.
     * @param nodeId Id of node.
     * @param hostname Hostname of node.
     * @param clientPort Client port.
     * @param replicaPort Replica port.
     * @return Return new {@link MessagingEnvironmentConfig}.
     */
    public MessagingEnvironmentConfig withNode(int nodeId, String hostname, int clientPort, int replicaPort) {
        Node[] newNodes = new Node[nodes.length + 1];
        for (int i = 0; i < nodes.length; i++) {
            newNodes[i] = nodes[i];
        }
        newNodes[nodes.length] = new Node(nodeId, hostname, replicaPort, clientPort);
        return new MessagingEnvironmentConfig(this.nodeId, newNodes);
    }

    /**
     * Check that config is initialized properly. (has at least one node and nodes list contains nodeId)
     * @return Return true if config is proper.
     */
    public boolean isInitializedProperly() {
        return nodes.length > 0 && Arrays.stream(nodes).anyMatch(node -> node.getId() == nodeId);
    }


    String getConfString() {
        StringBuilder builder = new StringBuilder();
        for (Node node : nodes) {
            builder.append(node);
        }
        // TODO: Temporary add same parameters as in benchmark
        builder.append(
                "IndirectConsensus = False\n" +
                "AugmentedPaxos = True\n" +
                "EDUR = False\n" +
                "BenchmarkRun = false\n" +
                "CrashModel = CrashStop\n" +
                "LogPath = jpaxosLogs\n" +
                "WindowSize = 2\n" +
                "BatchSize = 65507\n" +
                "MaxBatchDelay = 0\n" +
                "replica.ClientRequestBufferSize = 8212\n" +
                "ClientIDGenerator = ViewEpoch\n" +
                "FirstSnapshotEstimateBytes = 1024\n" +
                "SnapshotAskRatio = 100.0\n" +
                "SnapshotForceRatio = 200.0\n" +
                "MinimumInstancesForSnapshotRatioSample = 50\n" +
                "MinLogSizeForRatioCheckBytes = 10240\n" +
                "Network = NIO\n" +
                "MaxUDPPacketSize = 65507\n" +
                "RetransmitTimeoutMilisecs = 1000\n" +
                "TcpReconnectMilisecs = 1000\n" +
                "replica.SelectorThreads = -1\n" +
                "replica.ForwardClientRequests = true\n" +
                "replica.ForwardMaxBatchSize = 1450\n" +
                "replica.ForwardMaxBatchDelay = 5\n" +
                "TimeoutFetchBatchValue = 2500\n" +
                "FDSendTimeout = 2000\n" +
                "FDSuspectTimeout = 4000\n");
        return builder.toString();
    }
}
