package el.comm;


import el.comm.param.ClientParam;
import el.comm.worker.Connector;
import el.comm.worker.IoWorker;

import java.io.IOException;
import java.util.logging.Logger;

public class SocketCommClient {
    private Connector connector;
    private int ioThreadPoolSize = 1;
    private Logger logger = Logger.getLogger("CommLog");

    public SocketCommClient(int ioThreadPoolSize) {

        this.ioThreadPoolSize = ioThreadPoolSize;
        this.connector = new Connector();
        for (int i = 0; i < this.ioThreadPoolSize; i++) {
            IoWorker ioWorker = new IoWorker(i);
            connector.addWorker(ioWorker);
            new Thread(ioWorker).start();
            this.logger.info("[IoWorker-" + i + "]" + " start...");
        }
        new Thread(connector).start();
        this.logger.info("[Connector]" + " start...");
    }

    public void connect(String host, int port, ClientParam param) throws IOException {
        this.connector.connect(host, port, param);
    }

}
