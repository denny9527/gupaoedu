

package com.gupao.edu.vip.lion.monitor.jmx.stats;

/**
 */
public final class ServerStats {
    private long packetsSent;
    private long packetsReceived;
    private long maxLatency;
    private long minLatency = Long.MAX_VALUE;
    private long totalLatency = 0;
    private long count = 0;

    private final Provider provider;

    public interface Provider {
        long getOutstandingRequests();

        long getLastProcessedXid();

        String getState();

        int getNumAliveConnections();
    }

    public ServerStats(Provider provider) {
        this.provider = provider;
    }

    // getters
    synchronized public long getMinLatency() {
        return minLatency == Long.MAX_VALUE ? 0 : minLatency;
    }

    synchronized public long getAvgLatency() {
        if (count != 0) {
            return totalLatency / count;
        }
        return 0;
    }

    synchronized public long getMaxLatency() {
        return maxLatency;
    }

    public long getOutstandingRequests() {
        return provider.getOutstandingRequests();
    }

    public long getLastProcessedXid() {
        return provider.getLastProcessedXid();
    }

    synchronized public long getPacketsReceived() {
        return packetsReceived;
    }

    synchronized public long getPacketsSent() {
        return packetsSent;
    }

    public String getServerState() {
        return provider.getState();
    }

    /**
     * The number of client connections alive to this server
     */
    public int getNumAliveClientConnections() {
        return provider.getNumAliveConnections();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Latency min/avg/max: " + getMinLatency() + "/"
                + getAvgLatency() + "/" + getMaxLatency() + "\n");
        sb.append("Received: " + getPacketsReceived() + "\n");
        sb.append("Sent: " + getPacketsSent() + "\n");
        sb.append("Connections: " + getNumAliveClientConnections() + "\n");

        if (provider != null) {
            sb.append("Outstanding: " + getOutstandingRequests() + "\n");
            sb.append("xid: 0x" + Long.toHexString(getLastProcessedXid()) + "\n");
        }
        sb.append("Mode: " + getServerState() + "\n");
        return sb.toString();
    }

    // mutators
    synchronized void updateLatency(long requestCreateTime) {
        long latency = System.currentTimeMillis() - requestCreateTime;
        totalLatency += latency;
        count++;
        if (latency < minLatency) {
            minLatency = latency;
        }
        if (latency > maxLatency) {
            maxLatency = latency;
        }
    }

    synchronized public void resetLatency() {
        totalLatency = 0;
        count = 0;
        maxLatency = 0;
        minLatency = Long.MAX_VALUE;
    }

    synchronized public void resetMaxLatency() {
        maxLatency = getMinLatency();
    }

    synchronized public void incrementPacketsReceived() {
        packetsReceived++;
    }

    synchronized public void incrementPacketsSent() {
        packetsSent++;
    }

    synchronized public void resetRequestCounters() {
        packetsReceived = 0;
        packetsSent = 0;
    }

    synchronized public void reset() {
        resetLatency();
        resetRequestCounters();
    }
}
