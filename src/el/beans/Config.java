package el.beans;

import java.util.List;

/**
 * the Config Object
 */
public class Config {
    List<HostConfig> hosts;
    private int myId;
    private int timeout;
    private int learningInterval;
    private String dataDir;
    private boolean enableDataPersistence;

    public Config() {
    }

    public List<HostConfig> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostConfig> hosts) {
        this.hosts = hosts;
    }

    public int getMyId() {
        return myId;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getLearningInterval() {
        return learningInterval;
    }

    public void setLearningInterval(int learningInterval) {
        this.learningInterval = learningInterval;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public boolean isEnableDataPersistence() {
        return enableDataPersistence;
    }

    public void setEnableDataPersistence(boolean enableDataPersistence) {
        this.enableDataPersistence = enableDataPersistence;
    }
}
