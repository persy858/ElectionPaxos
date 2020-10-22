package el.comm.param;


import el.comm.inter.*;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Param {

    // logger
    private final Logger logger = Logger.getLogger("CommLog");

    private OnRead onRead;
    private OnReadError onReadError;
    private OnWrite onWrite;
    private OnWriteError onWriteError;
    private OnClose onClose;

    public OnReadError getOnReadError() {
        return onReadError;
    }

    public void setOnReadError(OnReadError onReadError) {
        this.onReadError = onReadError;
    }

    public OnWriteError getOnWriteError() {
        return onWriteError;
    }

    public void setOnWriteError(OnWriteError onWriteError) {
        this.onWriteError = onWriteError;
    }

    public OnRead getOnRead() {
        return onRead;
    }

    public void setOnRead(OnRead onRead) {
        this.onRead = onRead;
    }

    public OnWrite getOnWrite() {
        return onWrite;
    }

    public void setOnWrite(OnWrite onWrite) {
        this.onWrite = onWrite;
    }

    public OnClose getOnClose() {
        return onClose;
    }

    public void setOnClose(OnClose onClose) {
        this.onClose = onClose;
    }

    public void addLogHandler(Handler handler) {
        this.logger.addHandler(handler);
    }

    public void setLogLevel(Level level) {
        this.logger.setLevel(level);
    }

    public Logger getLogger() {
        return logger;
    }

    public abstract OnAccept getOnAccept();

    public abstract OnConnect getOnConnection();

    public abstract boolean isServerParam();
}
