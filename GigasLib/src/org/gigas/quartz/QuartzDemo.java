package org.gigas.quartz;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * 不能使用内部类或者不是主类的Job
 *
 */
public class QuartzDemo implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("QuartzDemo");
	}

	public static void main(String[] args) {
		//先启动定时线程
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = null;
		try {
			sched = sf.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		try {
			sched.start();
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		JobDetail jobDetail = JobBuilder.newJob(QuartzDemo.class).withIdentity("testJob_1", "group_1").build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger_1", "group_1").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10)
		// 时间间隔
				.withRepeatCount(5) // 重复次数(将执行6次)
				).build();

		try {
			sched.scheduleJob(jobDetail, trigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		JobDetail build = JobBuilder.newJob(QuartzJobDemo.class).withIdentity("QuartzDemo1", "group_2").build();
		SimpleTrigger build2 = TriggerBuilder.newTrigger().withIdentity("QuartzTrigger1", "group_2").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(10)).build();
		try {
			sched.scheduleJob(build, build2);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

}