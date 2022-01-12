package com.tmser.utils;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * @author tjx
 * @version 2021-12-01
 */
public class Identities {
	private static ThreadLocal<AtomicInteger> atomicIntThreadLocal = new ThreadLocal<>();
	private static ThreadLocal<Long> threadLocalId = new ThreadLocal<>();
	private static DecimalFormat threeFormatter = new DecimalFormat("000");
	private static DecimalFormat threadIdFormatter = new DecimalFormat("00000000");
	private static DecimalFormat autoFormatter = new DecimalFormat("0000");

	private static Integer lastSegment = null;
	private static ThreadLocalRandom localRandom = ThreadLocalRandom.current();
	private static int ID_LENGTH = 32;
	private static ThreadLocal<AtomicLong> increRpcIdThreadLocal = new ThreadLocal<>();
	private static ThreadLocal<String> currentRpcIdThreadLocal = new ThreadLocal<>();

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 * @return
	 */
	public static String uuid2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 使用SecureRandom随机生成Long. 
	 * @return
	 */
	public static long randomLong() {
		long v = localRandom.nextLong();
		if(v == Long.MIN_VALUE){
			return Long.MAX_VALUE;
		}
		return Math.abs(v);
	}

	/**
	 * 使用SecureRandom随机生成Int. 
	 * @return
	 */
	public static int randomInt() {
		int v = localRandom.nextInt();
		if(v == Integer.MIN_VALUE){
			return Integer.MAX_VALUE;
		}
		return Math.abs(v);
	}
	
	/**
	 * 基于Base62编码的SecureRandom随机生成bytes.
	 * @return
	 */
	public static String randomBase62(int length) {
		byte[] randomBytes = new byte[length];
		localRandom.nextBytes(randomBytes);
		return Encodes.encodeBase62(randomBytes);
	}


	/**
	 * 初始化数据
	 */
	public static void destory() {
        atomicIntThreadLocal.remove();
		threadLocalId.remove();
		increRpcIdThreadLocal.remove();
		currentRpcIdThreadLocal.remove();
	}

	/**
	 * 生成规则： yyyyMMddHHmmssSSS（17位） + 服务器IP D段（3位）+ threadId(8位)+ 自增计数器（4位）
	 * @return       115 20191115204435703 00000009  1342
	 *
	 */
	public static  String generateId() {
		final StringBuilder sb = new StringBuilder();
		sb.append(DateUtils.getCurrentDateTimeSSSAsLong());
		if(lastSegment == null ){
			lastSegment = Integer.valueOf(IpUtils.getLastServerIpSegment());
		}
		sb.append(threeFormatter.format(lastSegment));
		Long threadId = threadLocalId.get();
		if (threadId == null){
			threadId = Thread.currentThread().getId();
			threadLocalId.set(threadId);
		}
		sb.append(threadIdFormatter.format(threadId));

        AtomicInteger atomicInt = atomicIntThreadLocal.get();
		if (atomicInt == null || atomicInt.get() >= 9999){
            atomicInt = new AtomicInteger();
			atomicIntThreadLocal.set(atomicInt);
		}
		sb.append(autoFormatter.format(atomicInt.incrementAndGet()));
		String ret = sb.toString();
		int length = ret.length();
		if (length > ID_LENGTH){
			ret = ret.substring(length - ID_LENGTH,length);
		}
		return ret;
	}

	/**
	 * 生成一组不重复的数据
	 * @param total
	 * @return
	 */
	public static List<Integer> getRandomSequence(int total){
		int[] sequence = new int[total];
		List<Integer> output = Lists.newArrayList(total);
		for (int i = 0; i < total; i++){
			sequence[i] = i;
		}
		int end = total - 1;
		for (int i = 0; i < total; i++){
			int num = localRandom.nextInt(end + 1);
			output.add(sequence[num]);
			sequence[num] = sequence[end];
			end--;
		}
		return output;
	}
}
