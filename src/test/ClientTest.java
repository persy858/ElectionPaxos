package test;

import com.alibaba.fastjson.JSONObject;
import el.ElectionClient;
import el.exception.PaxosClientNullAddressException;
import el.packet.PacketBean;

import java.io.IOException;

public class ClientTest {

	public static void main(String[] args) {
		try {
			new Thread(() -> {
				ElectionClient client = null;
				try {
					client = new ElectionClient();
					client.setSendBufferSize(20);
					client.setRemoteAddress("localhost", 1024);
					client.submit(JSONObject.toJSONBytes(new MsgBean("put", "name", "M1")), 1);
					client.submit(JSONObject.toJSONBytes(new MsgBean("put", "name", "M2")), 1);
					client.submit(JSONObject.toJSONBytes(new MsgBean("put", "name", "M2")), 1);
					client.submit(JSONObject.toJSONBytes(new MsgBean("get", "name", "")), 1);
					client.flush(1);
				} catch (IOException | PaxosClientNullAddressException e) {
					e.printStackTrace();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
