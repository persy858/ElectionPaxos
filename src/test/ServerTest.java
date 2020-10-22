package test;

import el.ElectionServer;

import java.io.IOException;

public class ServerTest extends BaseTest{

	public static void main(String[] args) {
		try {
			ElectionServer server = new ElectionServer(getConfig(1));
			server.setGroupId(1, new KvCallback());
			server.setGroupId(2, new KvCallback());
			server.start();
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


}
