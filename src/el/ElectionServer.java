package el;

import el.beans.Config;
import el.beans.HostConfig;
import el.core.Acceptor;
import el.core.Learner;
import el.core.PaxosCallback;
import el.core.Proposer;
import el.packet.Packet;
import el.util.CommClient;
import el.util.CommServer;
import el.util.ObjectSerialize;
import el.util.ObjectSerializeImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ElectionServer {

    private Config config;

    private HostConfig hostConfig;

    private Map<Integer, PaxosCallback> groupidToCallback = new HashMap<>();
    private Map<Integer, Proposer> proposerMap = new HashMap<>();
    private Map<Integer, Learner> learnerMap = new HashMap<>();
    private Map<Integer, Acceptor> acceptorMap = new HashMap<>();
    private Logger logger = Logger.getLogger("ElectionServer");
    private ObjectSerialize objectSerialize = new ObjectSerializeImpl();

    private CommClient client;

    public ElectionServer(Config config) {
        this.config = config;
        this.hostConfig = getMyConfig();
        this.client = new CommClient(10);
    }


    public void setGroupId(int groupId, PaxosCallback executor) {
        Acceptor accepter = new Acceptor(hostConfig.getId(), config.getHosts(), hostConfig, config, groupId,
                this.client);
        Proposer proposer = new Proposer(hostConfig.getId(), config.getHosts(), hostConfig, config.getTimeout(),
                accepter, groupId, this.client);
        Learner learner = new Learner(hostConfig.getId(), config.getHosts(), hostConfig, config, accepter,
                executor, groupId, this.client);
        this.groupidToCallback.put(groupId, executor);
        this.acceptorMap.put(groupId, accepter);
        this.proposerMap.put(groupId, proposer);
        this.learnerMap.put(groupId, learner);
    }


    private HostConfig getMyConfig(){
        for (HostConfig hostConfig : config.getHosts()) {
            if (hostConfig.getId() == config.getMyId()) {
                return hostConfig;
            }
        }
        return null;
    }


    public void start() throws IOException, InterruptedException, ClassNotFoundException {
        // start the server
        CommServer server = new CommServer(this.hostConfig.getPort(), 4);
        System.out.println("paxos server-" + config.getMyId() + " start...");
        while (true) {
            byte[] data = server.recvFrom();
            Packet packet = objectSerialize.byteArrayToObject(data, Packet.class);
            int groupId = packet.getGroupId();
            Acceptor acceptor = this.acceptorMap.get(groupId);
            Proposer proposer = this.proposerMap.get(groupId);
            Learner learner = this.learnerMap.get(groupId);
            if (acceptor == null || proposer == null || learner == null) {
                return;
            }
            switch (packet.getWorkerType()) {
                case ACCEPTER:
                    acceptor.sendPacket(packet.getPacketBean());
                    break;
                case PROPOSER:
                    proposer.sendPacket(packet.getPacketBean());
                    break;
                case LEARNER:
                    learner.sendPacket(packet.getPacketBean());
                    break;
                case SERVER:
                    proposer.sendPacket(packet.getPacketBean());
                    break;
                default:
                    break;
            }
        }
    }
}
