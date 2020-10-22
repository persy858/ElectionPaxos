package el.comm.inter;

import el.comm.connection.Conn;

public interface OnClose {
    public void onClose(Conn conn);
}