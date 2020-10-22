package el.util;

import el.comm.SocketCommClient;
import el.comm.connection.Conn;
import el.comm.exception.ConnectionCloseException;
import el.comm.param.ClientParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommClient {
    private Map<String, Conn> addressToConn = new HashMap<>();
    private SocketCommClient client;

    public CommClient(int threadPoolSize) {
        this.client = new SocketCommClient(threadPoolSize);
    }


    public void sendTo(String ip, int port, byte[] msg) throws IOException {
        ClientParam param = new ClientParam();
        param.setLogLevel(Level.WARNING);
        param.setOnConnect(conn -> {
            String key = ip + ":" + port;
            this.addressToConn.put(key, conn);
        });
        String key = ip + ":" + port;
        if (!addressToConn.containsKey(key)) {
            client.connect(ip, port, param);
        }
        int count = 0;
        do {
            try {
                if (count >= 3)
                    break;
                while (addressToConn.get(key) == null);
                addressToConn.get(key).write(msg);
                break;
            } catch (ConnectionCloseException e) {
                e.printStackTrace();
                addressToConn.remove(key);
                client.connect(ip, port, param);
                count++;
            }
        } while (true);
    }

}
