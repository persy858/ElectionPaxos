package test;

import com.alibaba.fastjson.JSONObject;
import el.ElectionClient;
import el.exception.PaxosClientNullAddressException;

import java.io.IOException;

public class ClientTest {

	public static void main(String[] args) {
		try {
			ElectionClient client = new ElectionClient();
			client.setSendBufferSize(20);
			client.setRemoteAddress("localhost", 1024);
			client.submit(JSONObject.toJSONBytes(new MsgBean("put", "name", "Mike")), 1);
			client.submit(JSONObject.toJSONBytes(new MsgBean("put", "name", "Neo")), 1);
			client.submit(JSONObject.toJSONBytes(new MsgBean("get", "name", "")), 1);
			client.flush(1);
		} catch (IOException | PaxosClientNullAddressException e) {
			e.printStackTrace();
		}
	}
}
