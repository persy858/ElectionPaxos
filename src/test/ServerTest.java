package test;

import com.alibaba.fastjson.JSONObject;
import el.ElectionServer;
import el.beans.Config;
import el.util.FileUtils;

import java.io.IOException;

public class ServerTest extends BaseTest{

	public static void main(String[] args) {

		try {
			ElectionServer server = new ElectionServer(loadConfig("./config/server1-config.json"));
			server.setGroupId(1, new KvCallback());
//			server.setGroupId(2, new KvCallback());
			server.start();

		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}




}
