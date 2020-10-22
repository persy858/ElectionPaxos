package el.comm.inter;

import el.comm.connection.Conn;

public interface OnRead {
    public void onRead(Conn connection, byte[] data);
}

