package com.inextends.myratedlibrary;

import android.text.TextUtils;

public class ArrayUtils {

    public static String[] explode(String value) {
        return explode(value, ",");
    }

    public static String[] explode(String value, String delimiter) {
        return value.split(delimiter);
    }

    public static String implode(String... values) {
        return implode(",", values);
    }

    public static String implode(String delimiter, String... values) {
        if (values.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (TextUtils.isEmpty(values[i])) {
                continue;
            }
            sb.append(values[i]);
            if (i != values.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

}
