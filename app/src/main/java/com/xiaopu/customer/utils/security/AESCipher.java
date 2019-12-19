package com.xiaopu.customer.utils.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>AES加密类。</p>
 *
 * @author lightpole
 */
public final class AESCipher {

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding";
    private static final String CIPHER_TRANSFORMATION_NOPADDING = "AES/CBC/NOPadding";
    /**
     * <p>client key.</p>
     */
    public static byte[] clientKey_;
    /**
     * <p>server key.</p>
     */
    public static byte[] serverKey_;
    /**
     * <p>client iv.</p>
     */
    public static byte[] clientIv_;
    /**
     * <p>client iv.</p>
     */
    public static byte[] serverIv_;
    /**
     * <p>customer key.</p>
     */
    public static byte[] customerKey_;
    /**
     * <p>customer key.</p>
     */
    public static byte[] customerIv_;

    /**
     * <p>加密。</p>
     *
     * @param plantText 待加密字符串。
     * @param key       the key data.
     * @param iv        the buffer used as initialization vector.
     * @return encrypted data.
     * @throws Exception
     */
    public static String encrypt(String plantText, byte[] key, byte[] iv)
            throws Exception {
        byte[] plantBytes = plantText.getBytes("UTF-8");
        byte[] cipherBytes = encrypt(plantBytes, key, iv);

        return Base64.encode(cipherBytes);
    }

    /**
     * <p>加密。</p>
     *
     * @param cipherByts 待加密字符串。
     * @param key        the key data.
     * @param iv         the buffer used as initialization vector.
     * @return encrypted data.
     * @throws Exception
     */
    public static byte[] encrypt(byte[] cipherByts, byte[] key, byte[] iv)
            throws Exception {
        byte[] plantByts = null;
        Cipher cipher = initCipher(key, iv, Cipher.ENCRYPT_MODE);
        plantByts = cipher.doFinal(cipherByts);
        return plantByts;
    }

    /**
     * <p>解密。</p>
     *
     * @param cipherText 待解密字符串。
     * @param key        the key data.
     * @param iv         the buffer used as initialization vector.
     * @return decrypted data.
     * @throws Exception
     */
    public static String decrypt(String cipherText, byte[] key, byte[] iv)
            throws Exception {
        byte[] cipherBytes = Base64.decodeToBytes(cipherText);
        byte[] plantBytes = decrypt(cipherBytes, key, iv);

        return new String(plantBytes, "UTF-8");
    }

    /**
     * <p>解密。</p>
     *
     * @param cipherByts 待解密字符串。
     * @param key        the key data.
     * @param iv         the buffer used as initialization vector.
     * @return decrypted data.
     * @throws Exception
     */
    public static byte[] decrypt(byte[] cipherByts, byte[] key, byte[] iv)
            throws Exception {
        byte[] plantByts = null;
        Cipher cipher = initCipher(key, iv, Cipher.DECRYPT_MODE);
        plantByts = cipher.doFinal(cipherByts);
        return plantByts;
    }

    /**
     * <p>Init cipher.</p>
     *
     * @param key    the key data.
     * @param iv     the buffer used as initialization vector.
     * @param opMode the operation this cipher instance should be initialized for
     *               (one of: ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE or UNWRAP_MODE).
     * @return a cipher instance.
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    private static Cipher initCipher(byte[] key, byte[] iv, int opMode)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        AlgorithmParameterSpec spec = new IvParameterSpec(iv);
        SecretKey skey = new SecretKeySpec(key, "AES");
        cipher.init(opMode, skey, spec);
        return cipher;
    }
}
