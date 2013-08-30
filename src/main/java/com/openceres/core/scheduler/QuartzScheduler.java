package com.openceres.core.scheduler;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openceres.core.IScheduler;
import com.openceres.property.Const;
import com.openceres.util.ResourceBundleEx;

public class QuartzScheduler implements IScheduler {
	private static Logger LOG = LoggerFactory.getLogger(QuartzScheduler.class);

	static private QuartzScheduler schedulerService = null;
	StdSchedulerFactory sf = null;
	Scheduler sched = null;

	private QuartzScheduler() {
		try {

			sf = new StdSchedulerFactory();
			sf.initialize(ResourceBundleEx.getBundle().getProperties(Const.PROP_FILE_QUARTZ));

			sched = sf.getScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public QuartzScheduler getInstance() {
		if (schedulerService == null) {
			return new QuartzScheduler();
		} else {
			return schedulerService;
		}
	}

	public Scheduler getScheduler() {
		return sched;
	}

	public boolean startScheduler() {
		try {
			if (!sched.isStarted()) {
				sched.start();
			}
		} catch (SchedulerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return true;
	}

	public void clear() throws SchedulerException {
		sched.clear();
	}

	public boolean stopScheduler() {
		try {
			if (sched.isStarted()) {
				sched.shutdown();
			}
		} catch (SchedulerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return true;
	}

	public void updateSchedule(String groupId, String jobId, String cronExpression, Date startTime,
			Date endTime, int repeat) {
		try {
			if (!sched.checkExists(new JobKey(jobId, groupId))) {
				LOG.warn("Not Exsist schedule [" + groupId + "." + jobId + "]");
				return;
			}

			Trigger trigger = getTrigger(groupId, jobId, cronExpression, startTime, endTime, repeat);

			sched.rescheduleJob(new TriggerKey(jobId, groupId), trigger);
		} catch (SchedulerException e1) {
			LOG.error(e1.getMessage(), e1);
			return;
		}
	}

	public void updateSchedule(String groupId, String jobId, int interval, Date startTime,
			Date endTime, int repeat) {
		try {
			if (!sched.checkExists(new JobKey(jobId, groupId))) {
				LOG.warn("Not Exsist schedule [" + groupId + "." + jobId + "]");
				return;
			}

			Trigger trigger = getTrigger(groupId, jobId, interval, startTime, endTime, repeat);

			sched.rescheduleJob(new TriggerKey(jobId, groupId), trigger);
		} catch (SchedulerException e1) {
			LOG.error(e1.getMessage(), e1);
			return;
		}
	}

	public void updateSchedule(String groupId, String jobId, Date executeTime) {
		try {
			if (!sched.checkExists(new JobKey(jobId, groupId))) {
				LOG.warn("Not Exsist schedule [" + groupId + "." + jobId + "]");
				return;
			}

			Trigger trigger = getTrigger(groupId, jobId, executeTime);

			sched.rescheduleJob(new TriggerKey(jobId, groupId), trigger);
		} catch (SchedulerException e1) {
			LOG.error(e1.getMessage(), e1);
			return;
		}
	}

	public Trigger getTrigger(String triggerGroup, String triggerId, String cronExpression,
			Date startTime, Date endTime, int repeat) {
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerId, triggerGroup)
				.startAt(startTime).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.endAt(endTime).build();

		return trigger;
	}

	public Trigger getTrigger(String triggerGroup, String triggerID, int interval, Date startTime,
			Date endTime, int repeat) {
		Trigger trigger = null;
		
		// FIXME 꼭 파라미터를 모두 비교해야 하는가?????
		if (endTime == null && repeat == 0) {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)
									.repeatForever()).startAt(startTime).build();

		} else if (endTime != null && repeat == 0) {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
					.startAt(startTime).endAt(endTime).build();
		} else if (endTime == null && repeat != 0) {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)
									.withRepeatCount(repeat - 1)).startAt(startTime).build();
		} else {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)
									.withRepeatCount(repeat - 1)).startAt(startTime).endAt(endTime)
					.build();
		}

		return trigger;
	}

	public Trigger getTrigger(String triggerGroup, String triggerId, Date executeTime) {

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerId, triggerGroup)
				.startAt(executeTime).build();

		return trigger;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean addSchedule(String groupId, String jobId, String triggerGroup, String triggerId,
			String cronExpression, Date startTime, Date endTime, int repeat, Class jobClass) {
		try {
			if (sched.checkExists(new JobKey(jobId, groupId))) {
				LOG.warn("Already exist schedule [" + groupId + "." + jobId + "]");
			}
		} catch (SchedulerException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobId, groupId).build();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerId, triggerGroup)
				.startAt(startTime).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.endAt(endTime).build();

		try {
			sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			LOG.error(groupId + "." + jobId + " is register error [" + e.getMessage() + "]", e);
		}

		LOG.info("Schedule is added " + groupId + "." + jobId);

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean addSchedule(String groupId, String jobId, String triggerGroup, String triggerID,
			int interval, Date startTime, Date endTime, int repeat, Class jobClass) {
		try {
			if (sched.checkExists(new JobKey(jobId, groupId))) {
				LOG.warn("Already exist schedule [" + groupId + "." + jobId + "]");
			}
		} catch (SchedulerException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobId, groupId).build();
		Trigger trigger = null;

		// FIXME 꼭 파라미터를 모두 비교해야 하는가?????
		if (endTime == null && repeat == 0) {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)
									.repeatForever()).startAt(startTime).build();

		} else if (endTime != null && repeat == 0) {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
					.startAt(startTime).endAt(endTime).build();
		} else if (endTime == null && repeat != 0) {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)
									.withRepeatCount(repeat - 1)).startAt(startTime).build();
		} else {
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerID, triggerGroup)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval)
									.withRepeatCount(repeat - 1)).startAt(startTime).endAt(endTime)
					.build();
		}

		try {
			sched.scheduleJob(job, trigger);
		} catch (ObjectAlreadyExistsException e) {
			LOG.warn(groupId + "." + jobId + " is already registered.");
		} catch (SchedulerException e) {
			LOG.error(groupId + "." + jobId + " is register error [" + e.getMessage() + "]", e);
		}

		LOG.info("Schedule is added " + groupId + "." + jobId);

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean addScheduleOnce(String groupId, String jobId, String triggerGroup,
			String triggerId, Date executeTime, Class jobClass) {

		try {
			if (sched.checkExists(new JobKey(jobId, groupId))) {
				LOG.warn("Already exist schedule [" + groupId + "." + jobId + "]");
			}
		} catch (SchedulerException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobId, groupId).build();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerId, triggerGroup)
				.startAt(executeTime).build();

		try {
			sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			LOG.error(groupId + "." + jobId + " is register error [" + e.getMessage() + "]", e);
		}

		LOG.info("Schedule is added " + groupId + "." + jobId);

		return true;
	}

	public boolean deleteSchedule(String groupId, String jobId) {
		try {
			sched.deleteJob(new JobKey(jobId, groupId));
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.info("Schedule is deleted " + groupId + "." + jobId);

		return true;
	}
}
