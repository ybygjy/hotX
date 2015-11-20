package com.github.airst.CTools;

/**
 * Description: StringUtil
 * User: qige
 * Date: 15/11/20
 * Time: 上午11:14
 */
public class StringUtil {

    public static boolean isBlank(String str) {
        int strLen;
        if(str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if(!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNullStr(String str) {
        return str == null || "null".equals(str);
    }
}
