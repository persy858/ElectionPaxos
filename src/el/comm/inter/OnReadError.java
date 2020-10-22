package el.comm.inter;

import el.comm.connection.Conn;

public interface OnReadError {
    public void onReadError(Conn conn, Exception e);
}