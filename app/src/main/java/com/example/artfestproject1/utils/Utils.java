package com.example.artfestproject1.utils;

public class Utils {

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        // for (int i = bytes.length; i > 0; --i) {      debug this for very long time...
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

}