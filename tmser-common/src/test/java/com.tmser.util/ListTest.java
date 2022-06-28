package com.tmser.util;

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class ListTest {

    @Test
    public void testList() {
        List<String> of = Lists.newArrayList("1", "2", "3");
        for (int i = 0; i < of.size(); i++) {
            System.out.println(of.get(i));
        }

        of.remove(1);
        of.add(5, "5");
        for (int i = 0; i < of.size(); i++) {
            System.out.println(of.get(i));
        }
    }

    @Test
    public void testDate() {
        Date localDateTime = DateUtils.parseDate("2020-04-20 13:56");
        System.out.println(localDateTime.getTime() / 1000);
        String s = " ";
        System.out.println(Bytes.asList(s.getBytes(StandardCharsets.UTF_8)));
    }
}
