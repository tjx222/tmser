/**
 * 
 */
package com.tmser.timer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ��ʱ��������
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
	         // ��������ļ��н�startQuertz bean��lazy-init����Ϊfalse ����ʵ����  
	         // context.getBean("startQuertz");   
	    System.out.print( " Test end .. " );  
	}

}
