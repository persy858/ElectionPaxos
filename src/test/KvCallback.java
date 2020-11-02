package test;


import com.alibaba.fastjson.JSONObject;
import el.core.PaxosCallback;

import java.util.HashMap;
import java.util.Map;

public class KvCallback implements PaxosCallback {
    /**
     * the kv map store the key and value
     */
    private final Map<String, String> kv = new HashMap<>();

    @Override
    public void callback(byte[] msg) {
        /*
         * three method：get put and delete
         */
        String msString = new String(msg);
        MsgBean bean = JSONObject.parseObject(msString, MsgBean.class);
        switch (bean.getType()) {
            case "get":
                System.out.println("get voting:" + kv.get(bean.getKey()));
                break;
            case "put":
                kv.put(bean.getKey(), bean.getValue());
                System.out.println("put voting: "+ bean.getValue());
                break;
            case "delete":
                kv.remove(bean.getKey());
                System.out.println("delete ok");
                break;
            default:
                break;
        }
    }

}
