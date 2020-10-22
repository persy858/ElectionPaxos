package el.comm;


import el.comm.param.ServerParam;
import el.comm.worker.Acceptor;
import el.comm.worker.IoWorker;

import java.io.IOException;

public class SocketCommServer {
    private ServerParam param;
    private int ioThreadPoolSize = 1;

    public SocketCommServer(ServerParam serverParam, int ioThreadPoolSize) {
        this.param = serverParam;
        this.ioThreadPoolSize = ioThreadPoolSize;
    }

    /**
     * start server
     *
     * @throws IOException IOException
     */
    public void start() throws IOException {
        Acceptor accepter = new Acceptor(this.param);
        for (int i = 0; i < ioThreadPoolSize; i++) {
            IoWorker ioWorker = new IoWorker(i);
            accepter.addIoWorker(ioWorker);
            new Thread(ioWorker).start();
            this.param.getLogger().info("[IoWorker-" + i + "]" + " start...");
        }
        new Thread(accepter).start();
        this.param.getLogger().info("[Accepter] start...");
    }

}