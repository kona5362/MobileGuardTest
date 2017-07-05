package com.test.mobileguardtest.utils;

/**
 * Created by kona on 2017/7/3.
 */

public class ConvertSizeUtil {


    public static String convertSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        StringBuffer resultSize = new StringBuffer();
        if (size > gb) {
            String format = String.format("%d GB", size / gb);
            resultSize.append(format);
            size = size % gb;
            if (size > mb) {
                format = String.format("%d MB", size / mb);
                resultSize.append(format);
                size = size % mb;
                if (size > kb) {
                    format = String.format("%d KB", size / kb);
                    resultSize.append(format);
                    //size = size % kb;
                }
            }
        }
        return resultSize.toString();
    }
}
