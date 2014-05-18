package com.tmser.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 
 * @author tjx
 * @version 2.0
 * 2014-3-30
 */
public class Timer implements Job{
	
	public void method(){
		System.out.println("quartz running...");
	}
	
	public static void main(String[] args) {
		// System.out.println( " Test start  . " );  
	 //    ApplicationContext context = new ClassPathXmlApplicationContext("spring-core.xml");  
	         // context.getBean("startQuertz");   
		Calendar c = Calendar.getInstance();
		
	  Date d = new Date(1397145600000l);
	  Date now = new Date();
	  SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.print(s.format(d)+"  "+ now.getTime());  
	}

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		method();
		
	}

}
