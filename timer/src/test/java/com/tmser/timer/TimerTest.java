/**
 * 
 */
package com.tmser.timer;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.Trigger;
/**
 *
 * @author tjx
 * @version 2.0
 * 2014-4-2
 */
public class TimerTest {

	public void testCronTimer(){
		Trigger trigger = newTrigger()
	    .withIdentity("trigger3", "group1")
	    .withSchedule(cronSchedule("0 0/2 8-17 * * ?"))
	    .forJob("myJob", "group1")
	    .build();
	}
}
