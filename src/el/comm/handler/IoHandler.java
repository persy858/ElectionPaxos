package el.comm.handler;


import el.comm.connection.Connection;
import el.comm.connection.DataBag;
import el.comm.context.Context;
import el.comm.context.ContextBean;
import el.comm.inter.OnClose;
import el.comm.inter.OnRead;
import el.comm.inter.OnWrite;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.logging.Logger;

public class IoHandler {
    private Logger logger = Logger.getLogger("CommLog");
    private Context context;
    private Selector selector;
    private static final int BUFFER_SIZE = 1024;

    public IoHandler(Selector selector, Context context) {
        super();
        this.context = context;
        this.selector = selector;
    }

    /**
     * read data from remote site by channel
     *
     * @param channel channel
     * @param onRead  onRead
     * @param onClose onClose
     * @throws IOException ioexception
     */
    public void readDataFromRemoteSite(SocketChannel channel, OnRead onRead, OnClose onClose) throws IOException {
        // store current data
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        ContextBean bean = this.context.getChanToContextBean().get(channel);
        // read from remote side
        int count = channel.read(buffer);
        if (count >= 0) {
            // set buffer's position to 0
            buffer.flip();
            while (buffer.hasRemaining()) {
                DataBag bag = bean.getReadyToRead();
                bag.readFrom(buffer);
                if (bag.isFinish()) {
                    // finish read one data bag
                    bean.getAlreadyReadData().add(bag.getBytes());
                    bean.setReadyToRead(new DataBag());
                }
            }
            // call user's custom function
            Queue<byte[]> dataQueue = bean.getAlreadyReadData();
            while (!dataQueue.isEmpty()) {
                onRead.onRead(bean.getConnection(), dataQueue.poll());
            }
        } else {
            // read end
            try {
                closeRead(channel);
            } catch (ClosedChannelException e) {
                this.logger.warning("[Read Event] : " + e.toString());
                throw new IOException();
            }
            if (onClose != null)
                onClose.onClose(bean.getConnection());
        }
    }

    /**
     * write data to remote site
     *
     * @param channel channnel
     * @param onWrite onWrite
     * @throws IOException IOException
     */
    public void writeDataToRemoteSite(SocketChannel channel, OnWrite onWrite) throws IOException {
        ContextBean bean = this.context.getChanToContextBean().get(channel);
        Connection connection = bean.getConnection();
        // call write function when user define such function and haven't call
        // it yet!
        if (onWrite != null && !connection.isOnWriteCalled()) {
            connection.setOnWriteCalled(true);
            onWrite.onWrite(connection);
        }

        ByteBuffer buffer = connection.getReadyToWrite().peek();
        if (buffer != null) {
            if (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            // if this buffer finish write to buffer, delete it from queue
            if (!buffer.hasRemaining()) {
                connection.getReadyToWrite().poll();
            }
        }

        // nothing to write
        if (connection.getReadyToWrite().isEmpty()) {
            try {
                closeWrite(channel);
            } catch (ClosedChannelException e) {
                this.logger.warning("[Write Event] : " + e.toString());
                throw new IOException();
            }
            if (connection.isReadyToClose()) {
                connection.doClose();
            }
            return;
        }
    }

    /**
     * close write event
     *
     * @param channel channel
     * @throws ClosedChannelException ClosedChannelException
     */
    private void closeWrite(SocketChannel channel) throws ClosedChannelException {
        closeOps(channel, SelectionKey.OP_WRITE);
    }

    /**
     * close read event
     *
     * @param channel channel
     * @throws ClosedChannelException ClosedChannelException
     */
    private void closeRead(SocketChannel channel) throws ClosedChannelException {
        closeOps(channel, SelectionKey.OP_READ);
    }

    /**
     * close some operations
     *
     * @param channel    channel
     * @param opsToClose opsToClose
     * @throws ClosedChannelException ClosedChannelException
     */
    private void closeOps(SocketChannel channel, int opsToClose) throws ClosedChannelException {
        ContextBean bean = this.context.getChanToContextBean().get(channel);
        int ops = bean.getOps();
        ops = (~opsToClose) & ops;
        bean.setOps(ops);
        channel.register(this.selector, ops);
    }
}