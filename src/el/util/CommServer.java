package el.util;

import el.comm.SocketCommClient;
import el.comm.SocketCommServer;
import el.comm.param.ServerParam;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class CommServer {

    private BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private SocketCommServer server;
    private ServerParam serverParam;

    public CommServer(int port, int ioThreadPoolSize) throws IOException {
        this.serverParam = new ServerParam("localhost", port);
        this.serverParam.setLogLevel(Level.WARNING);
        this.serverParam.setBacklog(128);
        this.serverParam.setOnRead((conn, data) -> {
            queue.add(data);
        });
        this.serverParam.setOnClose(conn -> {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.server = new SocketCommServer(serverParam, ioThreadPoolSize);
        this.server.start();
    }

    public byte[] recvFrom() throws InterruptedException {
        return this.queue.take();
    }
}
