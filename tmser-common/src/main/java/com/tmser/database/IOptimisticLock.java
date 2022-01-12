package com.tmser.database;

/**
 * 对乐观锁的实现，在MyBatisInterceptor 实现
 */
public interface IOptimisticLock {

     Integer getVersion();
}
