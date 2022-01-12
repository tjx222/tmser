package com.tmser.util;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

public class IdentitiesTest {

    @Test
    public void uuid() {
        String uuid = Identities.uuid();
        String uuid1 = Identities.uuid();
        Assert.assertNotEquals(uuid, uuid1);
        Assert.assertEquals(36, uuid.length());
    }

    @Test
    public void uuid2() {
        String uuid = Identities.uuid2();
        String uuid1 = Identities.uuid2();
        Assert.assertNotEquals(uuid, uuid1);
        Assert.assertEquals(32, uuid.length());
    }

    @Test
    public void randomLong() {
        long l = Identities.randomLong();
        Assert.assertEquals(l+0,l);
    }

    @Test
    public void randomInt() {
        int l = Identities.randomInt();
        Assert.assertEquals(l+0,l);
    }

    @Test
    public void generateId() {
        String id = Identities.generateId();
        Assert.assertEquals(32, id.length());
    }

    @Test
    public void getRandomSequence() {
        List<Integer> randomSequence = Identities.getRandomSequence(3);
        HashSet<Integer> integers = Sets.newHashSet(randomSequence);
        Assert.assertEquals(3,integers.size());
    }
}