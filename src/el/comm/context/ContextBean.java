package el.comm.context;


import el.comm.connection.Connection;
import el.comm.connection.DataBag;
import el.comm.param.Param;

import java.util.Queue;

public class ContextBean {
    // a connection
    private Connection connection;
    // already read
    private Queue<byte[]> alreadyReadData;
    // ready to read
    private DataBag readyToRead;
    // operations
    private int ops;
    // params
    private Param param;
    public ContextBean(Connection connection, Queue<byte[]> alreadyReadData, DataBag readyToRead, int ops,
                       Param param) {
        super();
        this.connection = connection;
        this.alreadyReadData = alreadyReadData;
        this.readyToRead = readyToRead;
        this.ops = ops;
        this.param = param;
    }
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public Queue<byte[]> getAlreadyReadData() {
        return alreadyReadData;
    }
    public void setAlreadyReadData(Queue<byte[]> alreadyReadData) {
        this.alreadyReadData = alreadyReadData;
    }
    public DataBag getReadyToRead() {
        return readyToRead;
    }
    public void setReadyToRead(DataBag readyToRead) {
        this.readyToRead = readyToRead;
    }
    public int getOps() {
        return ops;
    }
    public void setOps(int ops) {
        this.ops = ops;
    }
    public Param getParam() {
        return param;
    }
    public void setParam(Param param) {
        this.param = param;
    }


}
