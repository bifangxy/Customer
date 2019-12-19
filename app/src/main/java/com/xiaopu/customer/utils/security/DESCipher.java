package com.xiaopu.customer.utils.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>DES加密类。</p>
 */
public class DESCipher {
    private Cipher cipher_;
    private static byte[] key_;
    private boolean isnopadding_;

    /**
     * <p>取得DESCipher类的一个实例。</p>
     *
     * @param nopadding 加密方式。
     * @return DESCipher类的一个实例。
     */
    private Cipher getInstance(boolean nopadding) {
        Cipher cipher;
        try {
            if (null == cipher_) {
                if (nopadding)
                    cipher = Cipher.getInstance("DES/ECB/NOPadding");
                else
                    cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
                return cipher;
            }
        } catch (NoSuchAlgorithmException e) {

        } catch (NoSuchPaddingException e) {

        }
        return cipher_;
    }

    /**
     * <p>加密字符串。</p>
     *
     * @param plantText 待加密的字符串。
     * @param key       加密密钥。
     * @return 加密后的字符串。
     */
    private String Encrypt(String plantText, byte[] key) {
        String cipherText = null;

        try {
            cipher_ = getInstance(isnopadding_);
            byte[] inblock = plantText.getBytes("UTF-8");
            cipher_.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DES"));
            byte[] outblock = callCipher(inblock);
            cipherText = Base64.encode(outblock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;

    }

    /**
     * <p>解密字符串。</p>
     *
     * @param cipherText 待解密的字符串。
     * @return 解密之后的比特数组。
     */
    private byte[] Decrypt(String cipherText) {
        byte[] inblock = null;
        byte[] outblock = null;
        try {
            cipher_ = getInstance(isnopadding_);
            inblock = Base64.decodeToBytes(cipherText);
            cipher_.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key_, "DES"));
            outblock = callCipher(inblock);
        } catch (Exception ex) {

        }
        return outblock;
    }

    /**
     * <p>调用加密方法。</p>
     *
     * @param data 待加密的比特数组。
     * @return 加密之后的比特数组。
     */
    private byte[] callCipher(byte[] data) {
        byte[] result = null;
        try {
            int size = cipher_.getOutputSize(data.length);
            result = new byte[size];
            int olen = cipher_.update(data, 0, data.length, result, 0);
            olen += cipher_.doFinal(result, olen);
            if (olen < size) {
                byte[] tmp = new byte[olen];
                System.arraycopy(result, 0, tmp, 0, olen);
                result = tmp;
            }
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * <p>进行加密。</p>
     *
     * @param plantText 待加密内容。
     * @param keyArray  加密密钥。
     * @return 密文。
     */
    public static String doEncrypt(String plantText, byte[] keyArray) {
        String cipherText = null;
        key_ = keyArray;
        DESCipher desCipher = new DESCipher();
        desCipher.isnopadding_ = false;
        cipherText = desCipher.Encrypt(plantText, key_);
        return cipherText;

    }

    /**
     * <p>进行解密。</p>
     *
     * @param cipherText 密文。
     * @param keyArr     解密密钥。
     * @return 明文。
     * @throws UnsupportedEncodingException 不支持的编码格式异常。
     */
    public static String doDecrypt(String cipherText, byte[] keyArr) throws UnsupportedEncodingException {
        String plantText = null;
        DESCipher desCipher = new DESCipher();
        key_ = keyArr;
        desCipher.isnopadding_ = false;
        plantText = new String(desCipher.Decrypt(cipherText), "UTF-8");
        return plantText;
    }

    /**
     * <p>decrypt without padding.</p>
     *
     * @param cipherText ciphered string.
     * @param keyArray   key used to decrypt.
     * @return decrypted string.
     */
    public static byte[] doDecryptWithoutPadding(String cipherText, byte[] keyArray) {
        byte[] plantText = null;
        key_ = keyArray;
        DESCipher desCipher = new DESCipher();
        desCipher.isnopadding_ = true;
        plantText = desCipher.Decrypt(cipherText);
        return plantText;
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp 构成该字符串的字节数组
     * @return 生成的密钥
     */
    public static byte[] getKey(byte[] arrBTmp) {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return arrB;
    }

}
