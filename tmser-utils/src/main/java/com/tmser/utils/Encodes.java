package com.tmser.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 封装各种格式的编码解码工具类.
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 * @author tjx
 * @version 2013-12-30
 */
public class Encodes {

	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	private static MessageDigest MD5;
	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
   
	/**
	    * 将密码加密
	    * @param str String
	    * @return String
	    */
	   public static String md5(String str){
			   MD5.update(str.getBytes());
			   byte b[] = MD5.digest();
			   int i;
			   StringBuilder buf = new StringBuilder();
			   for (int offset = 0; offset < b.length; offset++) {
			    i = b[offset];
			    if (i < 0)
			     i += 256;
			    if (i < 16)
			     buf.append("0");
			    buf.append(Integer.toHexString(i));
			   }
			   return buf.toString();
	   }
	   
		/**
	    * 将密码加密
	    * @param str String
	    * @return String
	    */
	   public static byte[] md5Byte(String str){
		   MD5.update(str.getBytes());
		   return MD5.digest();
	   }
	   
	/**
	 * Hex编码
	 * @param input
	 * @return
	 */
	public static String encodeHex(byte[] input) {
		return Hex.encodeHexString(input);
	}

	/**
	 * Hex解码.
	 * 
	 * @param input
	 * @return
	 */
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * Base64编码.
	 * @param input
	 * @return
	 */
	public static String encodeBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	/**
	 * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
	 * @param input
	 * @return
	 */
	public static String encodeUrlSafeBase64(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}

	/**
	 * Base64解码.
	 * @param input
	 * @return
	 */
	public static byte[] decodeBase64(String input) {
		return Base64.decodeBase64(input);
	}
	
	/**
	 * Base64解码.
	 * @param input
	 * @return 
	 */
	public static String decodeBase64ToString(String input) {
		return new String(Base64.decodeBase64(input));
	}
	
	/**
	 * Base64解码.
	 * @param input
	 * @param charset 字符编码
	 * @return
	 */
	public static String decodeBase64ToString(String input,Charset charset) {
		return new String(Base64.decodeBase64(input),charset);
	}

	/**
	 * Base62编码。
	 * @param input
	 * @return
	 */
	public static String encodeBase62(byte[] input) {
		char[] chars = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
		}
		return new String(chars);
	}

	/**
	 * Html 转码.
	 * @param html
	 * @return
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml4(html);
	}

	/**
	 * Html 解码.
	 * @param htmlEscaped
	 * @return
	 */
	public static String unescapeHtml(String htmlEscaped) {
		return StringEscapeUtils.unescapeHtml4(htmlEscaped);
	}

	/**
	 * Xml 转码.
	 * @param xml
	 * @return
	 */
	public static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml(xml);
	}

	/**
	 * Xml 解码.
	 * @param xmlEscaped
	 * @return
	 */
	public static String unescapeXml(String xmlEscaped) {
		return StringEscapeUtils.unescapeXml(xmlEscaped);
	}

	/**
	 * URL 编码, Encode默认为UTF-8. 
	 * @param part
	 * @return
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * URL 解码, Encode默认为UTF-8. 
	 * @param part
	 * @return
	 */
	public static String urlDecode(String part) {

		try {
			return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.unchecked(e);
		}
	}
}
