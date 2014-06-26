package org.gigas.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzJobDemo implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("QuartzJobDemo");
	}

}
