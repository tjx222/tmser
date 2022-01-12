package com.tmser.codec;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.crypto.Cipher;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 */
public abstract class AbstractCipherUtils implements CipherUtils {

    private static final int DEFAULT_POOL_MAX_SIZE = 5000;
    private static final int DEFAULT_POOL_IDLE_SIZE = 3000;

    protected static final Map<String, ObjectPool<Cipher>> modeCipherPoolMap = new HashMap<>();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    protected Cipher acquireCipher(String cipherMode) {
        try {
            ObjectPool<Cipher> pool = getCipherPool(cipherMode);
            return pool.borrowObject();
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new CipherException("failed to acquire AES cipher", ex);
        }
    }

    protected void releaseCipher(Cipher cipher, String cipherMode) {
        if (cipher != null) {
            try {
                ObjectPool<Cipher> pool = getCipherPool(cipherMode);
                pool.returnObject(cipher);
            } catch (Exception ignored) {
            }
        }
    }

    protected ObjectPool<Cipher> getCipherPool(String cipherMode) {
        rwLock.readLock().lock();
        try {
            ObjectPool<Cipher> pool = modeCipherPoolMap.get(cipherMode);
            if (pool != null) {
                return pool;
            }
        } finally {
            rwLock.readLock().unlock();
        }

        rwLock.writeLock().lock();
        try {
            // 再次尝试取pool
            ObjectPool<Cipher> pool = modeCipherPoolMap.get(cipherMode);
            if (pool != null) {
                return pool;
            }
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(DEFAULT_POOL_MAX_SIZE);
            config.setMaxIdle(DEFAULT_POOL_IDLE_SIZE);
            pool = new GenericObjectPool<>(new PooledCipherFactory(cipherMode), config);
            modeCipherPoolMap.put(cipherMode, pool);
            return pool;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
