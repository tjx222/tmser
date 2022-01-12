package com.tmser.codec;


import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;


public interface CipherUtils {

    static CipherUtils DES() {
        return DesCipherUtils.getInstance();
    }

    static CipherUtils AES() {
        return AesCipherUtils.getInstance();
    }

    Cipher acquireCipher();

    void releaseCipher(Cipher cipher);

    /**
     * 使用指定的key加密data，并返回base64字符串 (UTF-8)
     *
     * @param key  the key string
     * @param data the data string to be encrypted
     * @return encrypted base64 string
     */
    default String encryptBase64String(String key, String data) {
        return encryptBase64String(key, data, ICharset.defaultCharset());
    }

    /**
     * 使用指定的key加密data，并返回base64字符串
     *
     * @param key     the key string
     * @param data    the data string to be encrypted
     * @param charset charset to encode key and data string, use UTF-8 in case of not specified
     * @return encrypted base64 string
     */
    default String encryptBase64String(String key, String data, Charset charset) {
        return encryptBase64String(key, data, charset, null);
    }

    /**
     * 使用指定的key加密data，并返回base64字符串
     *
     * @param key     the key string
     * @param data    the data string to be encrypted
     * @param charset charset to encode key and data string, use UTF-8 in case of not specified
     * @param iv      the initialization vector
     * @return
     */
    default String encryptBase64String(String key, String data, Charset charset, String iv) {
        byte[] encrypted = encrypt(key, data, charset, iv);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 使用指定的key加密data，并返回hex字符串 (UTF-8)
     *
     * @param key  the key string
     * @param data the data string to be encrypted
     * @return encrypted hex string
     */
    default String encryptHexString(String key, String data) {
        return encryptHexString(key, data, ICharset.defaultCharset());
    }

    /**
     * 使用指定的key加密data，并返回hex字符串
     *
     * @param key     the key string
     * @param data    the data string to be encrypted
     * @param charset the charset to encode key and data strings
     * @return encrypted hex string
     */
    default String encryptHexString(String key, String data, Charset charset) {
        return encryptHexString(key, data, charset, null);
    }

    /**
     * 使用指定的key加密data，并返回hex字符串
     *
     * @param key     the key string
     * @param data    the data string to be encrypted
     * @param charset the charset to encode key and data strings
     * @param iv      the initialization vector
     * @return
     */
    default String encryptHexString(String key, String data, Charset charset, String iv) {
        byte[] encrypted = encrypt(key, data, charset, iv);
        return new String(Hex.encodeHex(encrypted));
    }


    /**
     * 使用指定的key加密data，编码使用UTF-8
     * 如果不指定charset，使用UTF-8
     *
     * @param key  the key string
     * @param data the data string to be encrypted
     * @return encrypted bytes
     */
    default byte[] encrypt(String key, String data) {
        return encrypt(key, data, ICharset.defaultCharset());
    }

    /**
     * 使用指定的key加密data
     * 如果不指定charset，使用UTF-8
     *
     * @param key     the key string
     * @param data    the data string to be encrypted
     * @param charset the charset to encode key and data strings
     * @return encrypted bytes
     */
    default byte[] encrypt(String key, String data, Charset charset) {
        return encrypt(key, data, charset, null);
    }

    /**
     * 使用指定的key加密data
     * 如果不指定charset，使用UTF-8
     * 如果不指定iv，默认null
     *
     * @param key     the key string
     * @param data    the data string to be encrypted
     * @param charset the charset to encode key and data strings
     * @param iv      the initialization vector
     * @return
     */
    default byte[] encrypt(String key, String data, Charset charset, String iv) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(data);
        if (charset == null) {
            charset = ICharset.defaultCharset();
        }
        byte[] k = key.getBytes(charset);
        byte[] d = data.getBytes(charset);
        byte[] v = null;
        if (iv != null) {
            v = iv.getBytes(charset);
        }
        return encrypt(k, d, v);
    }

    /**
     * 使用指定的key加密data，并返回base64字符串
     *
     * @param key  the key bytes
     * @param data the data bytes to be encrypted
     * @return encrypted base64 string
     */
    default String encryptBase64String(byte[] key, byte[] data) {
        byte[] encrypted = encrypt(key, data);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 使用指定的key加密data，并返回hex字符串
     *
     * @param key  the key bytes
     * @param data the data bytes to be encrypted
     * @return encrypted hex string
     */
    default String encryptHexString(byte[] key, byte[] data) {
        byte[] encrypted = encrypt(key, data);
        return new String(Hex.encodeHex(encrypted));
    }

    /**
     * 使用指定的key加密data
     *
     * @param key  the key bytes
     * @param data the data bytes to be encrypted
     * @return encrypted bytes
     */
    default byte[] encrypt(byte[] key, byte[] data) {
        return encrypt(key, data, null);
    }

    /**
     * 使用指定的key加密data
     *
     * @param key  the key bytes
     * @param data the data bytes to be encrypted
     * @param iv   the initialization vector
     * @return
     */
    byte[] encrypt(byte[] key, byte[] data, byte[] iv);

    /**
     * 使用指定的key解密base64字符串，使用UTF-8编码
     *
     * @param key    the key string
     * @param base64 the encrypted base64 string
     * @return decrypted string (UTF-8)
     */
    default String decryptBase64String(String key, String base64) {
        return decryptBase64String(key, base64, Charset.defaultCharset());
    }

    /**
     * 使用指定的key解密base64字符串
     * 如果不指定编码，使用UTF-8
     *
     * @param key     the key string
     * @param base64  the encrypted base64 string
     * @param charset the charset to decode decrypted bytes
     * @return decrypted string
     */
    default String decryptBase64String(String key, String base64, Charset charset) {
        return decryptBase64String(key, base64, charset, null);
    }

    /**
     * 使用指定的key解密base64字符串
     * 如果不指定编码，使用UTF-8
     * 如果不指定iv，默认null
     *
     * @param key     the key string
     * @param base64  the encrypted base64 string
     * @param charset the charset to decode decrypted bytes
     * @param iv      the initialization vector
     * @return
     */
    default String decryptBase64String(String key, String base64, Charset charset, String iv) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(base64);
        if (charset == null) {
            charset = ICharset.defaultCharset();
        }
        byte[] k = key.getBytes(charset);
        byte[] e;
        try {
            e = Base64.getDecoder().decode(base64);
            if (e.length == 0) {
                throw new Exception();
            }
        } catch (Exception ex) {
            throw new CipherException("No valid BASE64 string");
        }
        byte[] v = null;
        if (iv != null) {
            v = iv.getBytes(charset);
        }
        byte[] decrypted = decrypt(k, e, v);
        return new String(decrypted, charset);
    }

    /**
     * 使用指定的key解密hex字符串，使用UTF-8编码
     *
     * @param key the key string
     * @param hex the encrypted hex string
     * @return decrypted string (UTF-8)
     */
    default String decryptHexString(String key, String hex) {
        return decryptHexString(key, hex, ICharset.defaultCharset());
    }

    /**
     * 使用指定的key解密hex字符串
     * 如果不指定编码，使用UTF-8
     *
     * @param key     the key string
     * @param hex     the encrypted hex string
     * @param charset the charset to decode decrypted bytes
     * @return decrypted string
     */
    default String decryptHexString(String key, String hex, Charset charset) {
        return decryptHexString(key, hex, charset, null);
    }

    /**
     * 使用指定的key解密hex字符串
     * 如果不指定编码，使用UTF-8
     * 如果不指定iv，默认null
     *
     * @param key     the key string
     * @param hex     the encrypted hex string
     * @param charset the charset to decode decrypted bytes
     * @param iv      the initialization vector
     * @return
     */
    default String decryptHexString(String key, String hex, Charset charset, String iv) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(hex);
        if (charset == null) {
            charset = ICharset.defaultCharset();
        }
        byte[] k = key.getBytes(charset);
        byte[] e;
        try {
            e = Hex.decodeHex(hex.toCharArray());
        } catch (Exception ex) {
            throw new CipherException("No valid HEX string");
        }
        byte[] v = null;
        if (iv != null) {
            v = iv.getBytes(charset);
        }
        byte[] decrypted = decrypt(k, e, v);
        return new String(decrypted, charset);
    }


    /**
     * 使用指定的key解密data
     *
     * @param key     the key bytes
     * @param encoded the encrypted bytes
     * @return decrypted bytes
     */
    default byte[] decrypt(byte[] key, byte[] encoded) {
        return decrypt(key, encoded, null);
    }

    /**
     * 使用指定的key解密data
     *
     * @param key     the key bytes
     * @param encoded the encrypted bytes
     * @param iv      the initialization vector
     * @return
     */
    byte[] decrypt(byte[] key, byte[] encoded, byte[] iv);
}
