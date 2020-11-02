package test;

import el.ElectionServer;
import el.beans.Config;
import el.beans.HostConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server2 extends BaseTest{
	public static void main(String[] args) {
		try {
			ElectionServer server = new ElectionServer(loadConfig("./config/server2-config.json"));
			server.setGroupId(1, new KvCallback());
//			server.setGroupId(2, new KvCallback());
			server.start();
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
