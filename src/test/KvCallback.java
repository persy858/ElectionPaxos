package test;


import com.alibaba.fastjson.JSONObject;
import el.core.PaxosCallback;

import java.util.HashMap;
import java.util.Map;

public class KvCallback implements PaxosCallback {
    /**
     * 使用map来保存key与value映射
     */
    private Map<String, String> kv = new HashMap<>();

    @Override
    public void callback(byte[] msg) {
        /**
         * 一共提供了三种动作： get : 获取 put : 添加 delete : 删除
         */
        String msString = new String(msg);
        MsgBean bean = JSONObject.parseObject(msString, MsgBean.class);
        switch (bean.getType()) {
            case "get":
                System.out.println(kv.get(bean.getKey()));
                break;
            case "put":
                kv.put(bean.getKey(), bean.getValue());
                System.out.println("ok");
                break;
            case "delete":
                kv.remove(bean.getKey());
                System.out.println("ok");
                break;
            default:
                break;
        }
    }

}
