
package com.tmser.codec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;

class DesCipherUtils extends AbstractCipherUtils
    implements CipherUtils {


  static class Nested {
    private static final DesCipherUtils INSTANCE = new DesCipherUtils();
  }

//  static final DesCipherUtils INSTANCE = Nested.INSTANCE;
  static DesCipherUtils getInstance() {
    return Nested.INSTANCE;
  }

  private static final String DES_CIPHER_MODE = "DES";


  private DesCipherUtils() {
    getCipherPool(DES_CIPHER_MODE);
  }

  @Override
  public Cipher acquireCipher() {
    return acquireCipher(DES_CIPHER_MODE);
  }

  @Override
  public void releaseCipher(Cipher cipher) {
    releaseCipher(cipher, DES_CIPHER_MODE);
  }

  @Override
  public byte[] encrypt(byte[] key, byte[] data, byte[] iv) {
    validateKey(key);
    Objects.requireNonNull(data);
    Cipher cipher = null;
    try {
      Key secretKey = new SecretKeySpec(key, "DES");
      cipher = acquireCipher();
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return cipher.doFinal(data);
    } catch (Exception ex) {
      throw new CipherException("Failed to encrypt (DES)", ex);
    } finally {
      releaseCipher(cipher);
    }
  }

  @Override
  public byte[] decrypt(byte[] key, byte[] encoded, byte[] iv) {
    validateKey(key);
    validateEncoded(encoded);
    Cipher cipher = null;
    try {
      Key secretKey = new SecretKeySpec(key, "DES");
      cipher = acquireCipher();
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return cipher.doFinal(encoded);
    } catch (Exception ex) {
      throw new CipherException("Failed to decrypt (DES)", ex);
    } finally {
      releaseCipher(cipher);
    }
  }

  private void validateKey(byte[] key) {
    if (key == null || key.length != 8) {
      throw new CipherException("DES key size should be 64 bits");
    }
  }

  private void validateEncoded(byte[] encoded) {
    if (encoded == null || encoded.length % 8 != 0) {
      throw new CipherException("DES block size should be 64 bits");
    }
  }
}
