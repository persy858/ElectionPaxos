package el.comm.inter;

import el.comm.connection.Conn;

public interface OnWriteError {
    public void onWriteError(Conn conn, Exception e);
}
