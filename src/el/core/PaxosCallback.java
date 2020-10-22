package el.core;

public interface PaxosCallback {
    /**
     * the call back function
     *
     * @param msg msg data
     */
    public void callback(byte[] msg);
}
