package el;

import el.beans.Value;
import el.core.WorkerType;
import el.exception.PaxosClientNullAddressException;
import el.packet.Packet;
import el.packet.PacketBean;
import el.util.CommClient;
import el.util.ObjectSerialize;
import el.util.ObjectSerializeImpl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

public class ElectionClient {
    // the proposer host
    private String host;
    // proposer的port
    private int port;
    // comm client
    private CommClient commClient;
    // buffer's size
    private int bufferSize = 8;
    // buffer
    private Queue<byte[]> buffer;

    private int tmp = 0;

    private ObjectSerialize objectSerialize = new ObjectSerializeImpl();

    public ElectionClient() throws IOException {
        super();
        this.commClient = new CommClient(1);
        this.buffer = new ArrayDeque<>();
    }

    /**
     * set send buffer size
     *
     * @param bufferSize the buffer size
     */
    public void setSendBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * set the remote address
     *
     * @param host
     * @param port
     */
    public void setRemoteAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 刷新buffer
     *
     * @param groupId
     * @throws UnknownHostException
     * @throws IOException
     */
    public void flush(int groupId) throws UnknownHostException, IOException {
        if (this.buffer.isEmpty())
            return;
        UUID uuid = UUID.randomUUID();
        Packet packet = new Packet(new PacketBean("SubmitPacket", new Value(uuid, this.objectSerialize.objectToObjectArray(this.buffer))), groupId, WorkerType.SERVER);
        this.commClient.sendTo(this.host, this.port, this.objectSerialize.objectToObjectArray(packet));
        this.buffer.clear();
    }

    /**
     * submit the proposer
     *
     * @param value
     * @param groupId
     */
    public void submit(byte[] value, int groupId) throws PaxosClientNullAddressException, UnknownHostException, IOException {
        if (this.host == null)
            throw new PaxosClientNullAddressException();
        this.buffer.add(value);
        if (this.buffer.size() >= this.bufferSize) {
            flush(groupId);
        }
    }

}
