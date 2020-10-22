package test;

import el.beans.Config;
import el.beans.HostConfig;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    protected static Config getConfig(int myId){
        Config config = new Config();
        config.setMyId(myId);
        config.setTimeout(1000);
        config.setLearningInterval(1000);
        config.setDataDir("./dataDir/");
        config.setEnableDataPersistence(false);

        List<HostConfig> hosts = new ArrayList<>();

        HostConfig hostConfig = new HostConfig();
        hostConfig.setId(1);
        hostConfig.setHost("localhost");
        hostConfig.setPort(1024);
        hosts.add(hostConfig);

        hostConfig = new HostConfig();
        hostConfig.setId(2);
        hostConfig.setHost("localhost");
        hostConfig.setPort(1025);
        hosts.add(hostConfig);

        hostConfig = new HostConfig();
        hostConfig.setId(3);
        hostConfig.setHost("localhost");
        hostConfig.setPort(1026);
        hosts.add(hostConfig);

        config.setHosts(hosts);
        return config;
    }
}
