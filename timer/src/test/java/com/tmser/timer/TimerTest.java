/**
 * 
 */
package com.tmser.timer;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.*;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
/**
 *
 * @author tjx
 * @version 2.0
 * 2014-4-2
 */
public class TimerTest {

	public static void main(String[] args){
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		  

		  // define the job and tie it to our HelloJob class
		  JobDetail job = newJob(Timer.class)
		      .withIdentity("myJob", "group1")
		      .build();

		Trigger trigger = newTrigger()
	    .withIdentity("trigger3", "group1")
	    .withSchedule(cronSchedule("0/10 * * * * ?"))
	    .forJob("myJob", "group1")
	    .build();
		
		 Scheduler sched;
		try {
			sched = schedFact.getScheduler();
			sched.start();
			
			sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
