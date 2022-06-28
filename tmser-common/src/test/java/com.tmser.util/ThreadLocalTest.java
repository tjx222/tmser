package com.tmser.util;

public class ThreadLocalTest {

    public  void testLocal() throws InterruptedException {
        ThreadLocal<Integer>  tl1 = new ThreadLocal<>();
        tl1.set(20);
        fc();
        Thread.sleep( 3000);
    }

    void fc(){
        ThreadLocal tl2 = new ThreadLocal();
        tl2.get();
    }
}
