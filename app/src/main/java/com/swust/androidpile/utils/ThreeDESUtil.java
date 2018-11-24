package com.swust.androidpile.utils;
/**
 * Created by Administrator on 2018/1/4/004.
 */

import com.swust.androidpile.alipay.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ThreeDESUtil {

    private static String hexStr = "0123456789ABCDEF";
    // keybyte为加密密钥，长度为24字节
    private final static byte[] keybyte = "123456788765432112345678".getBytes();
    private static SecretKey deskey = new SecretKeySpec(keybyte, "DESede");

    // 加密(data.getBytes("utf-8")-->Base64)
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return Base64.encode(cipher.doFinal(data.getBytes("utf-8")));
        } catch (Exception e) {
            // 加密失败，打日志
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    public static String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return new String(cipher.doFinal(Base64.decode(data)), "utf-8");
        } catch (Exception e) {
            // 解密失败，打日志
            e.printStackTrace();
        }
        return null;
    }

//    // 16进制字符串转为字节数组
//    public static byte[] HexStringToBinary(String hexString) {
//        int len = hexString.length() / 2;
//        byte[] bytes = new byte[len];
//        byte high = 0;
//        byte low = 0;
//
//        for (int i = 0; i < len; i++) {
//            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
//            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
//            bytes[i] = (byte) (high | low);
//        }
//        return bytes;
//    }
//
//    // 字节数组转为16进制字符串
//    public static String bytesToHexString(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < bytes.length; i++) {
//            String hex = Integer.toHexString(0xFF & bytes[i]);
//            hex = hex.toUpperCase();
//            if (hex.length() == 1) {
//                sb.append('0');
//            }
//            sb.append(hex);
//        }
//        return sb.toString();
//    }
}