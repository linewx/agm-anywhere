package com.hp.alm.ali.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by lugan on 6/9/2014.
 */
public class CharUtils {
    public static boolean isAsciiAlphanumeric(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9');
    }

    public static boolean charsEqualIgnoreCase(char a, char b) {
        return a == b || toUpperCase(a) == toUpperCase(b) || toLowerCase(a) == toLowerCase(b);
    }

    public static char toUpperCase(char a) {
        if (a < 'a') {
            return a;
        }
        if (a <= 'z') {
            return (char)(a + ('A' - 'a'));
        }
        return Character.toUpperCase(a);
    }

    public static char toLowerCase(char a) {
        if (a < 'A' || a >= 'a' && a <= 'z') {
            return a;
        }

        if (a <= 'Z') {
            return (char)(a + ('a' - 'A'));
        }

        return Character.toLowerCase(a);
    }

    public static String encode(String val) {
        try {
            return URLEncoder.encode(val, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return val;
        }
    }
}
