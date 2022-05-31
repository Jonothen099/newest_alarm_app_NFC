package com.nfcalarmclock.statistics;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

/**
 * Data access object for storing when alarms were snoozed.
 */
@Dao
public interface NacAlarmSnoozedStatisticDao
	extends NacAlarmStatisticDao<NacAlarmSnoozedStatistic>
{

	/**
	 * Delete all rows from the table.
	 */
	@Query("DELETE FROM alarm_snoozed_statistic")
	int deleteAll();

	/**
	 * Get all instances when alarms were snoozed.
	 *
	 * @return All instances when alarms were snoozed.
	 */
	@Query("SELECT * FROM alarm_snoozed_statistic")
	LiveData<List<NacAlarmSnoozedStatistic>> getAll();

	/**
	 * Count the number of snoozed alarm statistics.
	 *
	 * @return The number of snoozed alarm statistics.
	 */
	@Query("SELECT COUNT(id) FROM alarm_snoozed_statistic")
	long getCount();

	/**
	 * Get the total snooze duration.
	 *
	 * @return The total snooze duration.
	 */
	@Query("SELECT SUM(duration) FROM alarm_snoozed_statistic")
	long getTotalDuration();

}
