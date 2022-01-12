
package com.tmser.codec;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;

class  AesCipherUtils extends AbstractCipherUtils
    implements CipherUtils {


  static class Nested {
    private static final AesCipherUtils INSTANCE = new AesCipherUtils();
  }

//  static final AesCipherUtils INSTANCE =   Nested.INSTANCE;
  static AesCipherUtils getInstance() {
    return Nested.INSTANCE;
  }

  private static final String AES_CIPHER_MODE = "AES";

  private AesCipherUtils() {
    getCipherPool(AES_CIPHER_MODE);
  }

  @Override
  public Cipher acquireCipher() {
    return acquireCipher(AES_CIPHER_MODE);
  }

  @Override
  public void releaseCipher(Cipher cipher) {
    releaseCipher(cipher, AES_CIPHER_MODE);
  }

  @Override
  public byte[] encrypt(byte[] key, byte[] data, byte[] iv) {
    validateKey(key);
    Objects.requireNonNull(data);
    Cipher cipher = null;
    try {
      Key secretKey = new SecretKeySpec(key, "AES");
      cipher = acquireCipher();
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return cipher.doFinal(data);
    } catch (Exception ex) {
      throw new CipherException("Failed to encrypt (AES)", ex);
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
      Key secretKey = new SecretKeySpec(key, "AES");
      cipher = acquireCipher();
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return cipher.doFinal(encoded);
    } catch (Exception ex) {
      throw new CipherException("Failed to decrypt (AES)", ex);
    } finally {
      releaseCipher(cipher);
    }
  }

  private void validateKey(byte[] key) {
    if (key == null || (key.length != 16 && key.length != 24 && key.length != 32)) {
      // 应该与出口限制有关，目前其实只支持128bits的key
      throw new CipherException("AES key size should be 128/192/256 bits");
    }
  }

  private void validateEncoded(byte[] encoded) {
    if (encoded == null || encoded.length % 16 != 0) {
      throw new CipherException("AES block size should be 128 bits");
    }
  }
}
