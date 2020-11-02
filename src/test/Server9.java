package test;


import el.ElectionServer;

import java.io.IOException;

public class Server9 extends BaseTest {
    public static void main(String[] args) {
        try {
            ElectionServer server = new ElectionServer(loadConfig("./config/server9-config.json"));
            server.setGroupId(1, new KvCallback());
//            server.setGroupId(2, new KvCallback());
            server.start();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}