package com.whlg.hospital.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String md5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (Exception ex) {
            throw new IllegalStateException("密码加密失败", ex);
        }
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encodedPassword.equals(rawPassword) || encodedPassword.equals(md5(rawPassword));
    }

    private static String toHex(byte[] bytes) {
        final char[] hexDigits = "0123456789ABCDEF".toCharArray();
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(hexDigits[(value >> 4) & 0x0F]);
            builder.append(hexDigits[value & 0x0F]);
        }
        return builder.toString();
    }
}