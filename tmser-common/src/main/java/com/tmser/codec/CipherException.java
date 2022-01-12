
package com.tmser.codec;

public class CipherException extends RuntimeException {
  private static final long serialVersionUID = -1093096950111676455L;

  public CipherException() {
  }

  public CipherException(String message) {
    super(message);
  }

  public CipherException(String message, Throwable cause) {
    super(message, cause);
  }

  public CipherException(Throwable cause) {
    super(cause);
  }
}
