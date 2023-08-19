package com.github.flyhero.toolbox.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.base.CaseFormat;

public class StringUtils {

    public static final String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;

    /*
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     */
    public static boolean isEmpty(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isEmpty(cs);
    }

    /*
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(cs);
    }

    /**
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     */
    public static String substringBefore(String str, String separator) {
        return org.apache.commons.lang3.StringUtils.substringBefore(str, separator);
    }

    /**
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     */
    public static String substringAfterLast(String str, String separator) {
        return org.apache.commons.lang3.StringUtils.substringAfterLast(str, separator);
    }

    /**
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     */
    public static String substringBetween(String str, String open, String close) {
        return org.apache.commons.lang3.StringUtils.substringBetween(str, open, close);
    }

    /*
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     */
    public static String substringBeforeLast(String str, String separator) {
        return org.apache.commons.lang3.StringUtils.substringBeforeLast(str, separator);
    }

    /**
     * 提取字符串中每个单词的首字母
     *
     * @param string extractWordFirstChar
     * @return ewfc
     */
    public static String extractWordFirstChar(String string) {
        string = lowerCamelToLowerUnderscore(string);
        String[] words = string.split("_");
        return Arrays.stream(words).map(word -> word.substring(0, 1)).collect(Collectors.joining());
    }

    /**
     * 转换字符串格式
     * lowerCamel -> lower_camel
     */
    public static String lowerCamelToLowerUnderscore(String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    /**
     * 转换字符串格式
     * UpperCamel --> upper_camel
     */
    public static String upperCamelToLowerUnderscore(String string) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    /**
     * 转换字符串格式
     * UpperCamel --> upperCamel
     */
    public static String upperCamelToLowerCamel(String string) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, string);
    }

    /**
     * 转换字符串格式
     * upperCamel --> UpperCamel
     */
    public static String lowerCamelToUpperCamel(String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, string);
    }

}
