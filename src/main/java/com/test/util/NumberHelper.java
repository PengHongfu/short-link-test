package com.test.util;

/*
 * MoXie (SysTem128@GMail.Com) 2009-8-11 11:59:20
 *
 * Copyright &copy; 2008-2010 Zoeey.Org
 * Code license: GNU Lesser General Public License Version 3
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 */

import com.google.common.hash.Hashing;
import lombok.val;

/**
 * 数字辅助工具<br />六十二进制内的数字互转（seo:62进制内的数字互转）
 *
 * @author MoXie
 */
public class NumberHelper {

    /**
     * 支持的最小进制
     */
    public static int MIN_RADIX = 2;
    /**
     * 支持的最大进制
     */
    public static int MAX_RADIX = 62;

    /**
     * 锁定创建
     */
    private NumberHelper() {
    }

    /**
     * 0-9a-zA-Z表示62进制内的 0到61。
     */
    private static final String num62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 返回一字符串，包含 number 以 toRadix 进制的表示。<br />
     * number 本身的进制由 fromRadix 指定。fromRadix 和 toRadix 都只能在 2 和 62 之间（包括 2 和 62）。<br />
     * 高于十进制的数字用字母 a-zA-Z 表示，例如 a 表示 10，b 表示 11 以及 Z 表示 62。<br />
     *
     * @param number    需要转换的数字
     * @param fromRadix 输入进制
     * @param toRadix   输出进制
     * @return 指定输出进制的数字
     */
    public static String baseConver(String number, int fromRadix, int toRadix) {
        long dec = any2Dec(number, fromRadix);
        return dec2Any(dec, toRadix);
    }

    /**
     * 返回一字符串，包含 十进制 number 以 radix 进制的表示。
     *
     * @param dec     需要转换的数字
     * @param toRadix 输出进制。当不在转换范围内时，此参数会被设定为 2，以便及时发现。
     * @return 指定输出进制的数字
     */
    public static String dec2Any(long dec, int toRadix) {
        if (toRadix < MIN_RADIX || toRadix > MAX_RADIX) {
            toRadix = 2;
        }
        if (toRadix == 10) {
            return String.valueOf(dec);
        }
        // -Long.MIN_VALUE 转换为 2 进制时长度为65
        char[] buf = new char[65];
        int charPos = 64;
        boolean isNegative = (dec < 0);
        if (!isNegative) {
            dec = -dec;
        }
        while (dec <= -toRadix) {
            buf[charPos--] = num62.charAt((int) (-(dec % toRadix)));
            dec = dec / toRadix;
        }
        buf[charPos] = num62.charAt((int) (-dec));
        if (isNegative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (65 - charPos));
    }

    /**
     * 返回一字符串，包含 number 以 10 进制的表示。<br />
     * fromBase 只能在 2 和 62 之间（包括 2 和 62）。
     *
     * @param number    输入数字
     * @param fromRadix 输入进制
     * @return 十进制数字
     */
    public static long any2Dec(String number, int fromRadix) {
        long dec = 0;
        long digitValue = 0;
        int len = number.length() - 1;
        for (int t = 0; t <= len; t++) {
            digitValue = num62.indexOf(number.charAt(t));
            dec = dec * fromRadix + digitValue;
        }
        return dec;
    }

    public static void main(String[] args) {
        long s = Hashing.murmur3_32().hashUnencodedChars("https://www.yuque.com/snailclimb/mf2z3k/kochkf")
                .padToLong();
        final val s1 = baseConver(String.valueOf(s), 10, 62);
        System.out.println(s1);
        System.out.println(baseConver(s1, 62, 10));
    }
}