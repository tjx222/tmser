
package com.tmser.codec;

import java.nio.charset.Charset;

public interface ICharset {

  Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  /**
   * DON'T CHANGE THIS VALUE! The default charset must be UTF-8
   *
   * @return the UTF-8 charset
   */
  static Charset defaultCharset() {
    return DEFAULT_CHARSET;
  }
}
