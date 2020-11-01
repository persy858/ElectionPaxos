package el.comm.connection;


import el.comm.context.Context;
import el.comm.context.ContextBean;
import el.comm.exception.ConnectionCloseException;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Connection implements Conn {
    private Context context;
    private SocketChannel channel;
    private Selector selector;
    private BlockingQueue<ByteBuffer> readyToWrite = new LinkedBlockingQueue<>();
    // write function could be call just once
    private boolean onWriteCalled = false;
    private boolean readyToClose = false;
    private Logger logger = Logger.getLogger("CommLog");

    public Connection(Context context, SocketChannel channel, Selector selector) {
        super();
        this.context = context;
        this.channel = channel;
        this.selector = selector;
    }

    public synchronized void write(byte[] data) throws ConnectionCloseException, ClosedChannelException {
        if (readyToClose)
            throw new ConnectionCloseException();
        ContextBean bean = context.getChanToContextBean().get(channel);
        if (bean == null) {
            readyToClose = true;
            throw new ConnectionCloseException();
        }
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 4);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        readyToWrite.add(buffer);
        int ops = bean.getOps();
        ops |= SelectionKey.OP_WRITE;
        bean.setOps(ops);
        this.channel.register(this.selector, ops);
        this.selector.wakeup();
    }

    /**
     * set close flag
     *
     */
    public void close() {
        this.readyToClose = true;
        if (this.readyToWrite.isEmpty()) {
            doClose();
        }
    }

    /**
     * close channel
     */
    public void doClose() {
        this.context.removeContextByChan(channel);
        try {
            this.channel.close();
        } catch (IOException e) {
            this.logger.warning("[Close Event] : " + e.toString());
        }
    }

    public BlockingQueue<ByteBuffer> getReadyToWrite() {
        return readyToWrite;
    }

    public boolean isOnWriteCalled() {
        return onWriteCalled;
    }

    public void setOnWriteCalled(boolean onWriteCalled) {
        this.onWriteCalled = onWriteCalled;
    }

    public boolean isReadyToClose() {
        return readyToClose;
    }

    public void setReadyToClose(boolean readyToClose) {
        this.readyToClose = readyToClose;
    }

    @Override
    public SocketAddress getLocalAddress() throws IOException {
        return this.channel.getLocalAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() throws IOException {
        return this.channel.getRemoteAddress();
    }

    /**
     * set send buffer's size
     */
    public void setSendBuffer(int size) throws IOException {
        this.channel.setOption(StandardSocketOptions.SO_SNDBUF, size);
    }

    /**
     * get send buffer' size
     *
     * @return
     * SO_SNDBUF
     * @throws IOException
     * IOException
     */
    public int getSendBuffer() throws IOException {
        return this.channel.getOption(StandardSocketOptions.SO_SNDBUF);
    }

    /**
     * set recv buffer's size
     */
    public void setRecvBuffer(int size) throws IOException {
        this.channel.setOption(StandardSocketOptions.SO_RCVBUF, size);
    }

    /**
     * get recv buffer's size
     *
     * @return
     * SO_RCVBUF
     * @throws IOException
     * IOException
     */
    public int getRecvBuffer() throws IOException {
        return this.channel.getOption(StandardSocketOptions.SO_RCVBUF);
    }

    /**
     * set keep alive
     */
    public void setKeepAlive(boolean flag) throws IOException {
        this.channel.setOption(StandardSocketOptions.SO_KEEPALIVE, flag);
    }

    /**
     * get keep alive
     *
     * @return
     * SO_KEEPALIVE
     * @throws IOException
     * IOException
     */
    public boolean getKeepAlive() throws IOException {
        return this.channel.getOption(StandardSocketOptions.SO_KEEPALIVE);
    }

    /**
     * set reuse address
     */
    public void setReUseAddr(boolean flag) throws IOException {
        this.channel.setOption(StandardSocketOptions.SO_REUSEADDR, flag);
    }

    /**
     * get reuse address
     *
     * @return
     * SO_REUSEADDR
     * @throws IOException
     * IOException
     */
    public boolean getReUseAddr() throws IOException {
        return this.channel.getOption(StandardSocketOptions.SO_REUSEADDR);
    }

    /**
     * set no delay
     */
    public void setNoDelay(boolean flag) throws IOException {
        this.channel.setOption(StandardSocketOptions.TCP_NODELAY, flag);
    }

    /**
     * get no delay
     *
     * @return
     * TCP_NODELAY
     * @throws IOException
     * IOException
     */
    public boolean getNoDelay() throws IOException {
        return this.channel.getOption(StandardSocketOptions.TCP_NODELAY);
    }
}