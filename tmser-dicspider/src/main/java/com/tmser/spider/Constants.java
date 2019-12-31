
package com.tmser.spider;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tjx1222
 * @version $Id: Constants.java, v 1.0 2017年2月5日 上午10:55:15 tjx1222 Exp $
 */
public abstract class Constants {
  /**
   * 部首对象列表
   */
  public final static String RADICAL_LIST = "radical_list";

  /**
   * 部首对象列表
   */
  public final static String USED_LEVEL_MAP = "USED_LEVEL_MAP";

  /**
   * 拼音对象列表
   */
  public final static String PINYIN_LIST = "pinyin_list";

  public final static Pattern YUANYIN_PTN = Pattern.compile("[āáǎàīíǐìōóǒòūúǔùēéěèǖǘǚǜaioueü]");
  public final static Map<String, String> YUANYIN_PINYIN_MAP = new HashMap<String, String>();
  public final static Map<String, Integer> PINYIN_SHENGDIAO_MAP = new HashMap<String, Integer>();
  static {
    YUANYIN_PINYIN_MAP.put("ā", "a");
    YUANYIN_PINYIN_MAP.put("á", "a");
    YUANYIN_PINYIN_MAP.put("ǎ", "a");
    YUANYIN_PINYIN_MAP.put("à", "a");
    YUANYIN_PINYIN_MAP.put("ī", "i");
    YUANYIN_PINYIN_MAP.put("í", "i");
    YUANYIN_PINYIN_MAP.put("ǐ", "i");
    YUANYIN_PINYIN_MAP.put("ì", "i");
    YUANYIN_PINYIN_MAP.put("ō", "o");
    YUANYIN_PINYIN_MAP.put("ó", "o");
    YUANYIN_PINYIN_MAP.put("ǒ", "o");
    YUANYIN_PINYIN_MAP.put("ò", "o");
    YUANYIN_PINYIN_MAP.put("ū", "u");
    YUANYIN_PINYIN_MAP.put("ú", "u");
    YUANYIN_PINYIN_MAP.put("ǔ", "u");
    YUANYIN_PINYIN_MAP.put("ù", "u");
    YUANYIN_PINYIN_MAP.put("ē", "e");
    YUANYIN_PINYIN_MAP.put("é", "e");
    YUANYIN_PINYIN_MAP.put("ě", "e");
    YUANYIN_PINYIN_MAP.put("è", "e");
    YUANYIN_PINYIN_MAP.put("ǖ", "ü");
    YUANYIN_PINYIN_MAP.put("ǘ", "ü");
    YUANYIN_PINYIN_MAP.put("ǚ", "ü");
    YUANYIN_PINYIN_MAP.put("ǜ", "ü");
    YUANYIN_PINYIN_MAP.put("ɑ", "a");
    YUANYIN_PINYIN_MAP.put("a", "a");
    YUANYIN_PINYIN_MAP.put("o", "o");
    YUANYIN_PINYIN_MAP.put("i", "i");
    YUANYIN_PINYIN_MAP.put("e", "e");
    YUANYIN_PINYIN_MAP.put("ü", "ü");
    YUANYIN_PINYIN_MAP.put("u", "u");

    PINYIN_SHENGDIAO_MAP.put("ā", 1);
    PINYIN_SHENGDIAO_MAP.put("á", 2);
    PINYIN_SHENGDIAO_MAP.put("ǎ", 3);
    PINYIN_SHENGDIAO_MAP.put("à", 4);
    PINYIN_SHENGDIAO_MAP.put("ī", 1);
    PINYIN_SHENGDIAO_MAP.put("í", 2);
    PINYIN_SHENGDIAO_MAP.put("ǐ", 3);
    PINYIN_SHENGDIAO_MAP.put("ì", 4);
    PINYIN_SHENGDIAO_MAP.put("ō", 1);
    PINYIN_SHENGDIAO_MAP.put("ó", 2);
    PINYIN_SHENGDIAO_MAP.put("ǒ", 3);
    PINYIN_SHENGDIAO_MAP.put("ò", 4);
    PINYIN_SHENGDIAO_MAP.put("ū", 1);
    PINYIN_SHENGDIAO_MAP.put("ú", 2);
    PINYIN_SHENGDIAO_MAP.put("ǔ", 3);
    PINYIN_SHENGDIAO_MAP.put("ù", 4);
    PINYIN_SHENGDIAO_MAP.put("ē", 1);
    PINYIN_SHENGDIAO_MAP.put("é", 2);
    PINYIN_SHENGDIAO_MAP.put("ě", 3);
    PINYIN_SHENGDIAO_MAP.put("è", 4);
    PINYIN_SHENGDIAO_MAP.put("ǖ", 1);
    PINYIN_SHENGDIAO_MAP.put("ǘ", 2);
    PINYIN_SHENGDIAO_MAP.put("ǚ", 3);
    PINYIN_SHENGDIAO_MAP.put("ǜ", 4);
    PINYIN_SHENGDIAO_MAP.put("ɑ", 0);
    PINYIN_SHENGDIAO_MAP.put("a", 0);
    PINYIN_SHENGDIAO_MAP.put("o", 0);
    PINYIN_SHENGDIAO_MAP.put("i", 0);
    PINYIN_SHENGDIAO_MAP.put("e", 0);
    PINYIN_SHENGDIAO_MAP.put("ü", 0);
    PINYIN_SHENGDIAO_MAP.put("u", 0);
  }
}
