package com.ltzk.mbsf.utils;



import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017-03-27.
 */

public class EncryptUtil {


    public final static String SEED = "PkT!ihpN^QkQ62k%";
    public static String encrypt(String cleartext) throws Exception {
        HbLogUtil.getInstance().e("http:request"+cleartext);
        return rep(encrypt(cleartext,SEED));
    }

    public static String encrypt(String cleartext,String seed) throws Exception {
        byte[] result = encrypt(seed.getBytes(), cleartext.getBytes());
        String base64String = Base64.encodeToString(result, Base64.NO_WRAP | Base64.URL_SAFE);
        return base64String;
    }

    public static String decrypt(String cleartext,String seed) throws Exception {
        byte[] enc = Base64.decode(cleartext.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);// Base64解码后
        byte[] result = decrypt(seed.getBytes(), enc);
        return new String(result,"UTF-8");
    }

    private static String rep(String base64){
        return base64.replaceAll("=","!");
    }

    /**
     * 先base64解码，在aes128解密
     * @param enc1
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] enc1) throws Exception {
        byte[] enc = Base64.decode(enc1, Base64.NO_WRAP);// Base64解码后
        byte[] result = decrypt(SEED.getBytes(), enc);
        return new String(result,"UTF-8");
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    /**
     * aes128解密
     * @param raw
     * @param encrypted
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "K*8#6Mltzk27&yP+";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
