package edu.put.paxosstm.messaging;

import lsr.common.PID;
import java.util.Arrays;

/**
 * Configuration of PaxosMessaging.
 */
public class MessagingConfig {
    final int nodeId;
    final PID[] nodes;

    /**
     * Create configuration for specific node.
     * @param nodeId Id of node (must be in nodes).
     */
    public MessagingConfig(int nodeId) {
        this(nodeId, null);
    }

    /**
     * Create configuration for specific node with list of nodes.
     * @param nodeId Id of node (must be in nodes).
     * @param nodes
     */
    private MessagingConfig(int nodeId, PID[] nodes) {
        this.nodeId = nodeId;
        if(nodes == null) {
            this.nodes = new PID[0];
        } else {
            this.nodes = nodes;
        }
    }

    /**
     * Create new {@link MessagingConfig} and add specified node.
     * This method may be chained to create config with multiple nodes.
     * @param nodeId Id of node.
     * @param hostname Hostname of node.
     * @param clientPort Client port.
     * @param replicaPort Replica port.
     * @return Return new {@link MessagingConfig}.
     */
    public MessagingConfig withNode(int nodeId, String hostname, int clientPort, int replicaPort) {
        PID[] newNodes = new PID[nodes.length + 1];
        for (int i = 0; i < nodes.length; i++) {
            newNodes[i] = nodes[i];
        }
        newNodes[nodes.length] = new PID(nodeId, hostname, replicaPort, clientPort);
        return new MessagingConfig(this.nodeId, newNodes);
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
        for (PID node : nodes) {
            builder.append(
                    String.format(
                            "process.%d = %s:%d:%d\n",
                            node.getId(),
                            node.getHostname(),
                            node.getClientPort(),
                            node.getReplicaPort()
                    )
            );
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
