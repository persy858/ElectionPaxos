package test;

import com.alibaba.fastjson.JSONObject;
import el.beans.Config;
import el.beans.HostConfig;
import el.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    protected static Config loadConfig(String fileName) {
        String configString = FileUtils.readFromFile(fileName);
        Config config = JSONObject.parseObject(configString, Config.class);
        System.out.println(config.toString());
        return config;
    }
}
