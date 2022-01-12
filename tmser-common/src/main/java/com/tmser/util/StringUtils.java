package com.tmser.util;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 *
 * @author tjx
 * @version 2013-12-30
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 转换为Double类型
     *
     * @param val
     * @return
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     *
     * @param val
     * @return
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     *
     * @param val
     * @return
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     *
     * @param val
     * @return
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }

    /**
     * 字符串转换为Integer数组
     *
     * @param val   字符串
     * @param regex 正则表达式
     * @return
     */
    public static Integer[] toIntegerArray(String val, String regex) {
        String[] valArr = val.split(regex);
        int arrLen = valArr.length;
        Integer[] it = new Integer[arrLen];
        for (int i = 0; i < arrLen; i++) {
            it[i] = Integer.parseInt(valArr[i].trim());
        }
        return it;
    }

    /**
     * 数组转成字符串
     * <p>
     * 可在打印日志的时候用
     * </p>
     *
     * @param args
     * @return
     */
    public static String argsToString(Object[] args) {
        StringBuilder s = new StringBuilder("args:[");
        for (Object o : args) {
            s.append(o).append(",");
        }
        s.append("]");

        return s.toString();
    }

    /**
     * null 转换为空字符
     *
     * @param o
     * @return
     */
    public static String nullToEmpty(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * 分割字符串，并转换为整形列表
     *
     * @param source
     * @param regex
     * @return
     */
    public static List<Integer> splitToIntegerList(String source, String regex) {
        List<Integer> rs = new ArrayList<Integer>();
        if (isNotEmpty(source)) {
            String[] arr = split(source, regex);
            for (String a : arr) {
                if (isNotEmpty(a)) {
                    rs.add(Integer.valueOf(a));
                }
            }
        }
        return rs;
    }


    /**
     * 检查指定的字符串列表是否不为空。
     */
    public static boolean isNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }


    /**
     * 只进行一次分割，例如： A=B=C时，只分割第一个=
     *
     * @param query
     * @param separator
     * @return
     */
    public static String[] splitOnce(String query, String separator) {
        int p = query.indexOf(separator);
        if (p > 0) {
            String key = query.substring(0, p);
            String value = query.substring(p + 1);
            return new String[]{key, value};
        } else {
            return new String[]{query};
        }
    }


    /**
     * 对一个数组内地值进行trim
     *
     * @param values
     * @return
     */
    public static String[] trim(String[] values) {
        String[] tv = new String[values.length];
        int i = 0;
        for (String vv : values) {
            tv[i++] = org.apache.commons.lang3.StringUtils.trimToEmpty(vv);
        }
        return tv;
    }

    // 从VM过来的有些数字类型可能是对象，而不是字符串
    public static boolean equals(Object me, String target) {
        if (null != me) {
            return org.apache.commons.lang3.StringUtils.equals(me.toString(), target);
        }
        return false;
    }

    /**
     * 当一个值与一组值进行比较时，只要有一个target与me相等，则返回true
     *
     * @param me
     * @param targets
     * @return
     */
    public static boolean equalsOr(String me, String[] targets) {
        for (String target : targets) {
            if (org.apache.commons.lang3.StringUtils.equals(me, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当一个值与一组值进行比较时，只要有一个target与me相等，则返回true
     *
     * @param me
     * @param targets
     * @return
     */
    public static boolean equalsOr(String me, List<String> targets) {
        for (String target : targets) {
            if (org.apache.commons.lang3.StringUtils.equals(me, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当一个值与一组值进行比较时，只要有一个target与me相等，则返回true
     *
     * @param me
     * @param targets 以','进行分割
     * @return
     */
    public static boolean equalsOr(String me, String targets) {
        return isNotEmpty(targets) && equalsOr(me, split(targets, ","));
    }

    /**
     * 当一个值与一组值进行比较时，必须所有target与me相等，才返回true
     *
     * @param me
     * @param targets
     * @return
     */
    public static boolean equalsAnd(String me, String[] targets) {
        for (String target : targets) {
            if (!org.apache.commons.lang3.StringUtils.equals(me, target)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 如果指定的target为NULL，使用默认值
     *
     * @param target
     * @param defaultValue
     * @return
     */
    public static String trim(String target, String defaultValue) {
        if (isBlank(target)) {
            return trim(defaultValue);
        }
        return trim(target);
    }

    private static char charInter = '\n';
    private static char charLt = '<';
    private static char charGt = '>';
    private static char charQuot = '"';
    private static char charAmp = '&';

    /**
     * 方法 toHtml 可以把源字符串中的不能在网页中正确显示的 字符替换为可以显示的相应字符串。
     *
     * @param strSource 替换前的字符串
     * @return 替换后的字符串
     */

    public static String toHtml(String strSource) {
        if (org.apache.commons.lang3.StringUtils.isBlank(strSource)) {
            return "";
        }
        StringBuilder strBufReturn = new StringBuilder();
        for (int i = 0; i < strSource.length(); i++) {
            if (strSource.charAt(i) == charInter)
                strBufReturn.append("<BR>");
            else if (strSource.charAt(i) == charLt)
                strBufReturn.append("<");
            else if (strSource.charAt(i) == charGt)
                strBufReturn.append(">");
            else if (strSource.charAt(i) == charQuot)
                strBufReturn.append("\"");
            else if (strSource.charAt(i) == charAmp)
                strBufReturn.append("&");
            else
                strBufReturn.append(strSource.charAt(i));
        }
        return strBufReturn.toString();
    }

    /**
     * 对空格、回车进行正确替换，其它HTML标记，直接转移为可显示字符串
     *
     * @param string
     * @return
     */
    public static String stringToHTMLString(String string) {
        if (isBlank(string)) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;
        boolean inHtml = false;
        for (int i = 0; i < len; i++) {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar && !inHtml) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == charLt) {
                    inHtml = true;
                    sb.append(c);
                } else if (c == charGt) {
                    inHtml = false;
                    sb.append(c);
                } else if (c == '&' && (i + 4) < len
                        && (string.charAt(i + 1) != 'n')
                        && (string.charAt(i + 2) != 'b')
                        && (string.charAt(i + 3) != 's')
                        && (string.charAt(i + 4) != 'p'))
                    sb.append("&amp;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("<br>");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160)
                        // nothing special only 7 Bit
                        sb.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(Integer.toString(ci));
                        sb.append(';');
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将不定数目的字符串连接起来
     *
     * @param args
     * @return
     */
    public static String join(String... args) {
        return org.apache.commons.lang3.StringUtils.join(args);
    }

    private static char CHAR_BlANK = ' ';
    private static char CHAR_DIVIDE = '/';
    private static char QM_DBC = '?', QM_SBC = '？';
    private static char COLON_DBC = ':', COLON_SBC = '：';
    private static char EQUAL_DBC = '=', EQUAL_SBC = '＝';
    private static char AMP_DBC = '&', AMP_SBC = '＆';
    private static char SQM_DBC = '\'', SQM_SBC = '‘';
    private static char DQM_DBC = '"', DQM_SBC = '”';

    /**
     * [" " , /]过滤掉 [: , ? , & , = , ' , "]转变为全角
     *
     * @param title
     * @return
     */
    public static String escapeTitleForSEO(String title) {
        title = trim(title);
        if (isNotBlank(title)) {
            StringBuilder sb = new StringBuilder(title.length());
            char[] chars = title.toCharArray();
            for (char c : chars) {
                if (c != CHAR_BlANK && c != CHAR_DIVIDE) {
                    if (c == COLON_DBC) {
                        sb.append(COLON_SBC);
                    } else if (c == QM_DBC) {
                        sb.append(QM_SBC);
                    } else if (c == AMP_DBC) {
                        sb.append(AMP_SBC);
                    } else if (c == EQUAL_DBC) {
                        sb.append(EQUAL_SBC);
                    } else if (c == SQM_DBC) {
                        sb.append(SQM_SBC);
                    } else if (c == DQM_DBC) {
                        sb.append(DQM_SBC);
                    } else {
                        sb.append(c);
                    }
                }
            }
            title = sb.toString();
        }
        return title;
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.trim().equals("") || "null".equals(s.trim()));
    }

    public static boolean isEmpty(Object ob) {
        return ob == null || ob.equals("") || "null".equals(ob);
    }

    public static boolean isNotEmpty(String s) {
        return s != null && (s.trim().length() > 0);
    }

    /**
     * 返回一个有效的字符串，将null转换为""。
     *
     * @param value - 原字符串。
     * @return 有效的字符串。
     */
    public static String getString(Object value) {
        return (value == null || "null".equals(value)) ? "" : value.toString();
    }

    public static String secureToString(Object o) {
        if (o == null) return null;
        return o.toString();
    }

    /**
     * for:{0}adf{1} 赋值
     *
     * @param pattern
     * @param values
     * @return
     */
    public static String format(String pattern, Object... values) {
        if (values == null || values.length == 0) {
            return MessageFormat.format(pattern, values);
        }
        String[] params = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            params[i] = getString(values[i]);
        }
        return MessageFormat.format(pattern, (Object[]) params);
    }

    /**
     * 转换成string类型
     *
     * @param o
     * @return
     */
    public static String toString(Object o) {
        if (o == null)
            return "";

        if (o instanceof String)
            return (String) o;
        return o.toString();
    }

    /**
     * 将 s 进行 BASE64 编码
     *
     * @param s
     * @return
     */
    public static String encodeByBase64(String s) {
        if (isEmpty(s)) {
            return null;
        }
        byte[] buffer = encodeByBase64(s.getBytes());
        return (new String(buffer));
    } //

    public static byte[] encodeByBase64(byte[] bs) {
        Base64.Encoder decoder = Base64.getEncoder();
        return decoder.encode(bs);
    }

    /**
     * 将 BASE64 编码的字符串 s 进行解码
     *
     * @param s
     * @return
     */
    public static String decodeByBase64(String s) {
        if (isEmpty(s)) {
            return null;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buffer = decoder.decode(s.getBytes());
        return (new String(buffer));
    }


    public static byte[] decoderByBase64(byte[] bs) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(bs);
    }


    public static boolean toBoolean(Object o) {
        if (o == null)
            return false;
        if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue();
        } else if (o instanceof Number) {
            return ((Number) o).intValue() == 1;
        }
        return o.toString().trim().equalsIgnoreCase("true");
    }

}
