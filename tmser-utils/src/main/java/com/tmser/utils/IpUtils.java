package com.tmser.utils;

import com.google.common.net.InetAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cool.chen on 2017/6/14 20:29.
 * modify history:
 */
public class IpUtils {
    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
    private static volatile InetAddress LOCAL_ADDRESS = null;
    public static final String ERROR_IP = "127.0.0.1";

    public IpUtils(){
    }
    public static final Pattern IP_PATTERN = Pattern.
            compile("(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})");

    public static boolean fromInternal(String ip) {
        InetAddress addr = InetAddresses.forString(ip);
        if(!addr.isLoopbackAddress() && !addr.isAnyLocalAddress()) {
            int address = InetAddresses.coerceToInteger(addr);
            return (address >>> 24 & 255) == 10 || (address >>> 24 & 255) == 172 && (address >>> 16 & 255) >= 16 && (address >>> 16 & 255) <= 31 || (address >>> 24 & 255) == 192 && (address >>> 16 & 255) == 168;
        } else {
            return true;
        }
    }

    public static boolean fromInternal(HttpServletRequest request) {
        String ip = getUserIPString(request);
        return fromInternal(ip);
    }

    public static Long getUserIPInt(HttpServletRequest request) {
        byte[] ip = getUserIPString(request).getBytes();
        long ip0 = (long)(ip[0] & 255);
        long ip1 = (long)(ip[1] & 255);
        long ip2 = (long)(ip[2] & 255);
        long ip3 = (long)(ip[3] & 255);
        return Long.valueOf((ip0 << 24) + (ip1 << 16) + (ip2 << 8) + ip3);
    }
    /**
     * 获取用户的真实ip
     * @param request
     * @return
     */
    public static String getUserIPString(HttpServletRequest request) {
        // 优先取X-Real-IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if("0:0:0:0:0:0:0:1".equals(ip))
                ip = ERROR_IP;
        }
        if ("unknown".equalsIgnoreCase(ip)) {
            ip = ERROR_IP;
            return ip;
        }
        int pos = ip.indexOf(',');
        if (pos >= 0) {
            ip = ip.substring(0, pos);
        }
        return ip;
    }
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var2) {
            return "unknown-host";
        }
    }
    public static InetAddress getLocalAddress() {
        if(LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        } else {
            InetAddress localAddress = getLocalAddress0();
            LOCAL_ADDRESS = localAddress;
            return localAddress;
        }
    }
    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if(isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Exception var6) {
            logger.warn("Failed to retriving ip address, " + var6.getMessage(), var6);
        }

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            if(e != null) {
                while(e.hasMoreElements()) {
                    try {
                        NetworkInterface e1 = (NetworkInterface)e.nextElement();
                        Enumeration addresses = e1.getInetAddresses();
                        if(addresses != null) {
                            while(addresses.hasMoreElements()) {
                                try {
                                    InetAddress e2 = (InetAddress)addresses.nextElement();
                                    if(isValidAddress(e2)) {
                                        return e2;
                                    }
                                } catch (Exception var5) {
                                    logger.warn("Failed to retriving ip address, " + var5.getMessage(), var5);
                                }
                            }
                        }
                    } catch (Exception var7) {
                        logger.warn("Failed to retriving ip address, " + var7.getMessage(), var7);
                    }
                }
            }
        } catch (Exception var8) {
            logger.warn("Failed to retriving ip address, " + var8.getMessage(), var8);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }
    private static boolean isValidAddress(InetAddress address) {
        if(address != null && !address.isLoopbackAddress()) {
            String name = address.getHostAddress();
            return name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches();
        } else {
            return false;
        }
    }
    public static String getLocalIp() {
        InetAddress address = getLocalAddress0();
        if (address != null){
            return address.getHostAddress();
        }
        return ERROR_IP;
    }

    public static long getLocalIpLong() {
        byte[] ip = getLocalIp().getBytes();
        long ip0 = (long)(ip[0] & 255);
        long ip1 = (long)(ip[1] & 255);
        long ip2 = (long)(ip[2] & 255);
        long ip3 = (long)(ip[3] & 255);
        return (ip0 << 24) + (ip1 << 16) + (ip2 << 8) + ip3;
    }

    public static String getLastIpSegment(HttpServletRequest request){
        String ip = getUserIPString(request);
        if(ip != null){
            ip = ip.substring(ip.lastIndexOf('.')+1);
        }else{
            ip = "0";
        }
        return ip;
    }
    public static Long ipToLong(String strIp) {
        try {
            byte[] ip = InetAddress.getByName(strIp).getAddress();
            long e = (long)(ip[0] & 255);
            long ip1 = (long)(ip[1] & 255);
            long ip2 = (long)(ip[2] & 255);
            long ip3 = (long)(ip[3] & 255);
            return Long.valueOf((e << 24) + (ip1 << 16) + (ip2 << 8) + ip3);
        } catch (Exception e ) {
            logger.error("No IP address available",e );
            return null;
        }
    }
    public static String getLastServerIpSegment(){
        String ip = getLocalIp();
        if(ip != null){
            ip = ip.substring(ip.lastIndexOf('.')+1);
        }else{
            ip = "0";
        }
        return ip;
    }

    /**
     * 判断我们获取的ip是否是一个符合规则ip
     * @param ip
     * @return
     */
    public static boolean isValidIP(String ip) {
        if (StringUtils(ip)) {
            logger.debug("ip is null. valid result is false");
            return false;
        }

        Matcher matcher = IP_PATTERN.matcher(ip);
        boolean isValid =  matcher.matches();
        logger.debug("valid ip:" + ip + " result is: " + isValid);
        return isValid;
    }
}

