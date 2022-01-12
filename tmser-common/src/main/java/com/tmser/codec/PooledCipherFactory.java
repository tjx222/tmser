package com.tmser.codec;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import javax.crypto.Cipher;

class PooledCipherFactory extends BasePooledObjectFactory<Cipher> {

    private String cipherMode;

    public PooledCipherFactory(String cipherMode) {
        this.cipherMode = cipherMode;
    }

    @Override
    public Cipher create() throws Exception {
        try {
            return Cipher.getInstance(cipherMode);
        } catch (Exception ex) {
            throw new CipherException("failed to acquire cipher", ex);
        }
    }

    @Override
    public PooledObject<Cipher> wrap(Cipher obj) {
        return new DefaultPooledObject<>(obj);
    }
}
