package el.comm.param;


import el.comm.inter.OnAccept;
import el.comm.inter.OnConnect;
import el.comm.inter.OnConnectError;

public class ClientParam extends Param {
    private OnConnect onConnect;
    private OnConnectError onConnectError;

    public ClientParam() {
        //
    }

    public OnConnect getOnConnect() {
        return onConnect;
    }

    public void setOnConnect(OnConnect onConnect) {
        this.onConnect = onConnect;
    }

    public OnConnectError getOnConnectError() {
        return onConnectError;
    }

    public void setOnConnectError(OnConnectError onConnectError) {
        this.onConnectError = onConnectError;
    }



    @Override
    public OnAccept getOnAccept() {
        return null;
    }

    @Override
    public OnConnect getOnConnection() {
        return this.onConnect;
    }


    @Override
    public boolean isServerParam() {
        return false;
    }

}