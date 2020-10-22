package el.core;


import el.beans.Config;
import el.beans.HostConfig;
import el.beans.Value;
import el.packet.LearnRequest;
import el.packet.LearnResponse;
import el.packet.Packet;
import el.packet.PacketBean;
import el.util.CommClient;
import el.util.ObjectSerialize;
import el.util.ObjectSerializeImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Learner {

	// learner's id
	private int id;

	// accepter's number
	private int accepterNum;

	// accepters
	private List<HostConfig> learners;

	// 学习到的临时状态 instanceid -> id -> value
	private Map<Integer, Map<Integer, Value>> tmpState = new HashMap<>();

	// 学习的状态
	private Map<Integer, Value> state = new HashMap<>();

	// 学习到的instance
	private volatile int currentInstance = 1;

	// learner配置信息
	private HostConfig my;

	/**
	 * 全局配置文件信息
	 */
	private Config confObject;

	/**
	 * 当前节点的accepter
	 */
	private Acceptor acceptor;

	/**
	 * 定时线程
	 */
	private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	/**
	 * 状态执行者
	 */
	private PaxosCallback executor;

	// 组id
	private int groupId;

	private ObjectSerialize objectSerialize = new ObjectSerializeImpl();
	
	private Logger logger = Logger.getLogger("MyPaxos");
	
	// 客户端
	private CommClient client;
	
	// 消息队列，保存packetbean
	private BlockingQueue<PacketBean> msgQueue = new LinkedBlockingQueue<>();

	public Learner(int id, List<HostConfig> learners, HostConfig my, Config confObject, Acceptor acceptor,
                   PaxosCallback executor, int groupId, CommClient client) {
		super();
		this.id = id;
		this.accepterNum = learners.size();
		this.learners = learners;
		this.my = my;
		this.confObject = confObject;
		this.acceptor = acceptor;
		this.executor = executor;
		this.groupId = groupId;
		this.client = client;
		service.scheduleAtFixedRate(() -> {
			// 广播学习请求
			sendRequest(this.id, this.currentInstance);
		} , confObject.getLearningInterval(), confObject.getLearningInterval(), TimeUnit.MILLISECONDS);
		new Thread(() -> {
			while (true) {
				try {
					PacketBean msg = msgQueue.take();
					recvPacket(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 向消息队列中插入packetbean
	 * @param bean
	 * @throws InterruptedException
	 */
	public void sendPacket(PacketBean bean) throws InterruptedException {
		this.msgQueue.put(bean);
	}

	/**
	 * 处理接收到的packetbean
	 * 
	 * @param bean
	 */
	public void recvPacket(PacketBean bean) {
		switch (bean.getType()) {
		case "LearnRequest":
			//LearnRequest request = gson.fromJson(bean.getData(), LearnRequest.class);
			LearnRequest request = (LearnRequest) bean.getData();
			Value value = null;
			if (acceptor.getAcceptedValue().containsKey(request.getInstance()))
				value = acceptor.getAcceptedValue().get(request.getInstance());
			sendResponse(request.getId(), request.getInstance(), value);
			break;
		case "LearnResponse":
			//LearnResponse response = gson.fromJson(bean.getData(), LearnResponse.class);
			LearnResponse response = (LearnResponse) bean.getData();
			onResponse(response.getId(), response.getInstance(), response.getValue());
			break;
		default:
			System.err.println("Unknown Type!");
			break;
		}
	}

	/**
	 * 发送请求
	 * 
	 * @param instance
	 */
	private void sendRequest(int id, int instance) {
		//this.tmpState.remove(instance);
		PacketBean packetBean = new PacketBean("LearnRequest", new LearnRequest(id, instance));
		byte[] data;
		try {
			data = this.objectSerialize.objectToObjectArray(new Packet(packetBean, this.groupId, WorkerType.LEARNER));
			learners.forEach((info) -> {
				try {
					this.client.sendTo(info.getHost(), info.getPort(), data);
				} catch (IOException e) {
					//
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 发送响应
	 * 
	 * @param peerId
	 *            对端的id
	 * @param instance
	 * @param value
	 */
	private void sendResponse(int peerId, int instance, Value value) {
		HostConfig peer = getSpecLearner(peerId);
		try {
			PacketBean packetBean = new PacketBean("LearnResponse", new LearnResponse(id, instance, value));
			this.client.sendTo(peer.getHost(), peer.getPort(),
					this.objectSerialize.objectToObjectArray(new Packet(packetBean, this.groupId, WorkerType.LEARNER)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取id为特定值的learner信息
	 * 
	 * @param id
	 * @return
	 */
	private HostConfig getSpecLearner(int id) {
		for (HostConfig each : this.learners) {
			if (each.getId() == id) {
				return each;
			}
		}
		return null;
	}

	/**
	 * 响应返回值
	 * 
	 * @param peerId
	 * @param instance
	 * @param value
	 */
	private void onResponse(int peerId, int instance, Value value) {
		if (!this.tmpState.containsKey(instance)) {
			this.tmpState.put(instance, new HashMap<>());
		}
		if (value == null)
			return;
		Map<Integer, Value> map = this.tmpState.get(instance);
		map.put(peerId, value);
		Map<Value, Integer> count = new HashMap<>();
		map.forEach((k, v) -> {
			if (!count.containsKey(v)) {
				count.put(v, 1);
			} else {
				count.put(v, count.get(v) + 1);
			}
		});
		count.forEach((k, v) -> {
			if (v >= this.accepterNum / 2 + 1) {
				this.state.put(instance, k);
				// 当learner学习成功时，让accepter也去同步这个状态
				this.acceptor.getAcceptedValue().put(instance, k);
				Instance acceptInstance = this.acceptor.getInstanceState().get(instance);
				if (acceptInstance == null) {
					this.acceptor.getInstanceState().put(instance, new Instance(1, k, 1));
				} else {
					acceptInstance.setValue(k);
				}
				if (instance == currentInstance) {
					// 调用paxos状态执行者
					this.logger.info("[onResponse success]" + " " + peerId + " " + instance + " " + value);
					handleCallback(k);
					currentInstance++;
				}
			}
		});
	}
	
	/**
	 *  调用paxos状态执行者 
	 * @param value
	 */
	private void handleCallback(Value value) {
		byte[] data = value.getData();
		Queue<byte[]> values;
		try {
			values = this.objectSerialize.byteArrayToObject(data, Queue.class);
			values.forEach(v -> {
				this.executor.callback(v);
			});
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
