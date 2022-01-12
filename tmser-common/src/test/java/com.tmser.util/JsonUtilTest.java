package com.tmser.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class JsonUtilTest {

    private static String defaultRefundOrderJson = "[{\"type\": 1, \"typeName\": \"现金\", \"refundOrder\": 1, \"consumeOrder\": 5}," +
            " {\"type\": 2, \"typeName\": \"刷卡\", \"refundOrder\": 4, \"consumeOrder\": 2}, " +
            "{\"type\": 3, \"typeName\": \"支票\", \"refundOrder\": 5, \"consumeOrder\": 1}," +
            " {\"type\": 4, \"typeName\": \"支付宝\", \"refundOrder\": 3, \"consumeOrder\": 3}," +
            " {\"type\": 5, \"typeName\": \"微信\", \"refundOrder\": 2, \"consumeOrder\": 4}," +
            "{\"type\": 6, \"typeName\": \"余额\", \"refundOrder\": 6, \"consumeOrder\": 0}]";

    @AllArgsConstructor
    static class A {
        private Integer age;

        private String name;


        public String getMode(){
            return "auto";
        }

    }

    @Data
    static class PayTypeOrder {

        private Integer type;

        private String typeName;

        private Integer refundOrder;

        private Integer consumeOrder;
    }

    @Test
    public void testJSON(){

        A a = new A(1, "test");
        ObjectMapper defaultMapper = JsonUtil.getDefaultMapper();
        JsonUtil.configure(defaultMapper);
        defaultMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String s = JsonUtil.toJson(defaultMapper, a);

        Assert.assertEquals("{\"age\":1,\"name\":\"test\",\"mode\":\"auto\"}", s);

    }

    @Test
    public void testListJson(){
        List<PayTypeOrder> payTypeOrderList = JsonUtil.toListObject(defaultRefundOrderJson, PayTypeOrder.class);

        Assert.assertNotNull(payTypeOrderList);
        Assert.assertEquals(payTypeOrderList.size(),6);

    }
}
