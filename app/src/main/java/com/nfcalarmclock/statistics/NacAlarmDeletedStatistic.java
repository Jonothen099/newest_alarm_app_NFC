package com.nfcalarmclock.statistics;

import androidx.room.Entity;

import com.nfcalarmclock.alarm.NacAlarm;

/**
 * Statistics for when an alarm is deleted.
 */
@Entity(tableName="alarm_deleted_statistic",
	ignoredColumns={"alarm_id"})
public class NacAlarmDeletedStatistic
	extends NacAlarmStatistic
{

	/**
	 */
	public NacAlarmDeletedStatistic()
	{
		super();
	}

	/**
	 */
	public NacAlarmDeletedStatistic(NacAlarm alarm)
	{
		super(alarm);
	}

}
