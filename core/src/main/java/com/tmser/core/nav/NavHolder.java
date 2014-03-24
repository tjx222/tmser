package com.tmser.core.nav;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 导航缓存类
 *
 * @author tjx
 * @version 2.0
 * 2014-1-7
 */
public class NavHolder implements Serializable {
	private static final long serialVersionUID = 2625257676412095439L;
	
	private final static Map<String,Nav> navs = new HashMap<String,Nav>();
	
    public static Nav findNav(String navid){
    	return Collections.unmodifiableMap(navs).get(navid);
    }
    
    public static Map<String,Nav> getAllNavs(){
    	return Collections.unmodifiableMap(navs);
    }
	
    public void addNavConfig(Nav nav) {
    	navs.put(nav.getId(),nav);
    }
    
    public static Map<String, Nav> getNavMap(){
    	return navs;
    }
    
    public static void clear(){
    	navs.clear();
    }
    
    public static int size(){
    	return navs.size();
    }
}
