package com.tmser.utils;

import com.google.common.collect.Lists;
import org.codehaus.commons.compiler.util.StringUtil;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * @author tjx
 * @version 2014-02-10
 */
public class Identities {

	private static ThreadLocal<AtomicLong> atomicLongThreadLocal = new ThreadLocal<AtomicLong>();
	private static ThreadLocal<Long> threadLocalId = new ThreadLocal<Long>();
	private static DecimalFormat threeFormatter = new DecimalFormat("000");
	private static DecimalFormat threadIdFormatter = new DecimalFormat("00000000");
	private static DecimalFormat autoFormatter = new DecimalFormat("0000");

	private static Integer lastSegment = null;
	private static ThreadLocalRandom localRandom = ThreadLocalRandom.current();
	private static int id_Length = 32;
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
		atomicLongThreadLocal.remove();
		threadLocalId.remove();
		increRpcIdThreadLocal.remove();
		currentRpcIdThreadLocal.remove();
	}

	/**
	 * 生成规则： yyyyMMddHHmmssSSS（17位） + 服务器IP D段（3位）+ threadId(8位)+ 自增计数器（4位）
	 * @return       115 20191115204435703 00000009  1342
	 *
	 */
	public static  String generateBizId() {
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

		AtomicLong atomicLong = atomicLongThreadLocal.get();
		if (atomicLong == null || atomicLong.get() >= 9999){
			atomicLong = new AtomicLong();
			atomicLongThreadLocal.set(atomicLong);
		}
		sb.append(autoFormatter.format(atomicLong.incrementAndGet()));
		String ret = sb.toString();
		int length = ret.length();
		if (length > id_Length){
			ret = ret.substring(length - id_Length,length);
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

	/**
	 * 同一个应用内使用 1.1 -> 1.2
	 * 从header 获取到的值，给header赋值 负责分发值
	 * @return
	 */
	public static String getRpcId(){
		String nowRpcId = RequestData.getRequest().getHeader(TraceConstant.X_RPC_ID);
		return getIncrementRpcId(nowRpcId);
	}

	/**
	 * 为下一个服务调用 当前为1.1
	 * 下一个为  1.1.1
	 * 再下一个为 1.1.2
	 * @return
	 */
	public static String getRpcIdAddSpan(){
		String nowRpcId = currentRpcIdThreadLocal.get();
		if (StringUtils.isEmpty(nowRpcId)){
			nowRpcId = RequestData.getRequest().getHeader(TraceConstant.X_RPC_ID);
			nowRpcId = nowRpcId == null ? "1" : nowRpcId;
		}
		nowRpcId= nowRpcId +".1";
		currentRpcIdThreadLocal.set(nowRpcId);
		return nowRpcId;
	}

	/**
	 * 同一个服务内  1.1 -> 1.2
	 * 1.1.1 -> 1.1.2
	 * @param nowRpcId
	 * @return
	 */
	public static String getIncrementRpcId(String nowRpcId){
		if (StringUtil.isEmpty(nowRpcId)){
			nowRpcId = RequestData.getRequest().getHeader(TraceConstant.X_RPC_ID);
		}

		String nextRpcId = "1";
		if (StringUtil.isEmpty(nowRpcId)){
			return nextRpcId;
		}
		AtomicLong atomicLong = increRpcIdThreadLocal.get();
		if (atomicLong == null){
			atomicLong = new AtomicLong();
			increRpcIdThreadLocal.set(atomicLong);
		}
		String preRpacId = nowRpcId.equals(nextRpcId)? nextRpcId:nowRpcId.substring(0,nowRpcId.lastIndexOf("."));
		StringBuilder stringBuilder = new StringBuilder(preRpacId);
		stringBuilder.append(".").append(atomicLong.incrementAndGet());
		currentRpcIdThreadLocal.set(stringBuilder.toString());
		return stringBuilder.toString();
	}
}
