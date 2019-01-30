
package com.gupao.edu.vip.lion.monitor.jmx.mxbean;

/**
 *
 */
public interface ServerMXBean {
    /**
     * @return the server socket port number
     */
    String getClientPort();

    /**
     * @return the server version
     */
    String getVersion();

    /**
     * @return time the server was started
     */
    String getStartTime();

    /**
     * @return min request latency in ms
     */
    long getMinRequestLatency();

    /**
     * @return average request latency in ms
     */
    long getAvgRequestLatency();

    /**
     * @return max request latency in ms
     */
    long getMaxRequestLatency();

    /**
     * @return number of packets received so far
     */
    long getPacketsReceived();

    /**
     * @return number of packets sent so far
     */
    long getPacketsSent();

    /**
     * @return number of outstanding requests.
     */
    long getOutstandingRequests();

    /**
     * Current TickTime of server in milliseconds
     */
    int getTickTime();

    /**
     * Set TickTime of server in milliseconds
     */
    void setTickTime(int tickTime);

    /**
     * Current maxClientCnxns allowed from a particular host
     */
    int getMaxClientCnxnsPerHost();

    /**
     * Set maxClientCnxns allowed from a particular host
     */
    void setMaxClientCnxnsPerHost(int max);

    /**
     * Current minSessionTimeout of the server in milliseconds
     */
    int getMinSessionTimeout();

    /**
     * Set minSessionTimeout of server in milliseconds
     */
    void setMinSessionTimeout(int min);

    /**
     * Current maxSessionTimeout of the server in milliseconds
     */
    int getMaxSessionTimeout();

    /**
     * Set maxSessionTimeout of server in milliseconds
     */
    void setMaxSessionTimeout(int max);

    /**
     * Reset packet and latency statistics
     */
    void resetStatistics();

    /**
     * Reset min/avg/max latency statistics
     */
    void resetLatency();

    /**
     * Reset max latency statistics only.
     */
    void resetMaxLatency();

    /**
     * @return number of alive client connections
     */
    long getNumAliveConnections();
}
