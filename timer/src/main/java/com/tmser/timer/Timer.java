/**
 * 
 */
package com.tmser.timer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 定时器主程序
 * @author tjx
 * @version 2.0
 * 2014-3-30
 */
public class Timer {
	
	public void method(){
		System.out.println("quartz running...");
	}
	
	public static void main(String[] args) {
		 System.out.println( " Test start  . " );  
	     ApplicationContext context = new ClassPathXmlApplicationContext("spring-core.xml");  
	         // 如果配置文件中将startQuertz bean的lazy-init设置为false 则不用实例化  
	         // context.getBean("startQuertz");   
	    System.out.print( " Test end .. " );  
	}

}
