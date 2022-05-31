package com.nfcalarmclock.system;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;

import com.nfcalarmclock.alarm.NacAlarm;
import com.nfcalarmclock.activealarm.NacActiveAlarmActivity;
import com.nfcalarmclock.activealarm.NacActiveAlarmService;
import com.nfcalarmclock.main.NacMainActivity;
import com.nfcalarmclock.nfc.NacNfc;
import com.nfcalarmclock.nfc.NacNfcTag;
import com.nfcalarmclock.shared.NacSharedDefaults;
import com.nfcalarmclock.shared.NacSharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;

/**
 */
@SuppressWarnings("RedundantSuppression")
public class NacIntent
{

	/**
	 * Tag name for retrieving a NacAlarm from a bundle.
	 */
	public static final String ALARM_BUNDLE_NAME = "NacAlarmBundle";

	/**
	 * Tag name for retrieving a media path from a bundle.
	 */
	public static final String MEDIA_BUNDLE_NAME = "NacMediaBundle";

	/**
	 * Add an alarm to an intent.
	 */
	public static Intent addAlarm(Intent intent, Bundle bundle)
	{
		if ((intent != null) && (bundle != null))
		{
			intent.putExtra(ALARM_BUNDLE_NAME, bundle);
		}

		return intent;
	}

	/**
	 * Add an alarm to an intent.
	 *
	 * @param  intent  An intent.
	 * @param  alarm  An alarm.
	 *
	 * @return The passed in intent with the alarm.
	 */
	public static Intent addAlarm(Intent intent, NacAlarm alarm)
	{
		return NacIntent.addAlarm(intent, NacBundle.toBundle(alarm));
	}

	/**
	 * Create an intent that will be used to start the Alarm activity.
	 *
	 * @param  context  A context.
	 * @param  bundle  A bundle.
	 *
	 * @return The Alarm activity intent.
	 */
	public static Intent createAlarmActivity(Context context, Bundle bundle)
	{
		Intent intent = new Intent(context, NacActiveAlarmActivity.class);
		int flags = Intent.FLAG_ACTIVITY_NEW_TASK
			| Intent.FLAG_ACTIVITY_CLEAR_TASK;

		intent.addFlags(flags);
		return NacIntent.addAlarm(intent, bundle);
	}

	/**
	 * @see NacIntent#createAlarmActivity(Context, Bundle)
	 */
	public static Intent createAlarmActivity(Context context, NacAlarm alarm)
	{
		Bundle bundle = NacBundle.toBundle(alarm);

		return NacIntent.createAlarmActivity(context, bundle);
	}

	/**
	 * Create an intent that will be used to start the foreground alarm service.
	 *
	 *
	 * @param  context  A context.
	 * @param  bundle  A bundle.
	 *
	 * @return The Foreground service intent.
	 */
	public static Intent createForegroundService(Context context, Bundle bundle)
	{
		Intent intent = new Intent(NacActiveAlarmService.ACTION_START_SERVICE, null,
			context, NacActiveAlarmService.class);

		return NacIntent.addAlarm(intent, bundle);
	}

	/**
	 * @see NacIntent#createForegroundService(Context, Bundle)
	 */
	@SuppressWarnings("unused")
	public static Intent createForegroundService(Context context, NacAlarm alarm)
	{
		Bundle bundle = NacBundle.toBundle(alarm);

		return NacIntent.createForegroundService(context, bundle);
	}

	/**
	 * Create an intent that will be used to start the Main activity.
	 *
	 * @param  context  A context.
	 * @param  bundle  A bundle.
	 *
	 * @return The Main activity intent.
	 */
	public static Intent createMainActivity(Context context, Bundle bundle)
	{
		Intent intent = new Intent(context, NacMainActivity.class);
		int flags = Intent.FLAG_ACTIVITY_NEW_TASK
			| Intent.FLAG_ACTIVITY_CLEAR_TASK;

		intent.addFlags(flags);
		return NacIntent.addAlarm(intent, bundle);
	}

	/**
	 * @see NacIntent#createMainActivity(Context, Bundle)
	 */
	public static Intent createMainActivity(Context context)
	{
		return NacIntent.createMainActivity(context, (Bundle) null);
	}

	/**
	 * @see NacIntent#createMainActivity(Context, Bundle)
	 */
	public static Intent createMainActivity(Context context, NacAlarm alarm)
	{
		Bundle bundle = NacBundle.toBundle(alarm);
		return NacIntent.createMainActivity(context, bundle);
	}

	/**
	 * @return An intent that will be used to dismiss the alarm activity.
	 */
	public static Intent dismissAlarmActivity(Context context, NacAlarm alarm)
	{
		Intent intent = NacIntent.createAlarmActivity(context, alarm);

		intent.setAction(NacActiveAlarmActivity.ACTION_DISMISS_ACTIVITY);
		return intent;
	}

	/**
	 * @return An intent that will be used to dismiss the alarm activity with NFC.
	 */
	public static Intent dismissAlarmActivityWithNfc(Context context,
		NacNfcTag tag)
	{
		NacAlarm activeAlarm = tag.getActiveAlarm();
		String action = tag.getNfcAction();
		Intent intent = NacIntent.createAlarmActivity(context, activeAlarm);

		//intent.setAction(NacActiveAlarmActivity.ACTION_DISMISS_ACTIVITY);
		intent.setAction(action);
		NacNfc.addTagToIntent(intent, tag.getNfcTag());

		return intent;
	}

	/**
	 * @return An intent that will be used to dismiss the foreground alarm service.
	 */
	public static Intent dismissForegroundService(Context context, NacAlarm alarm)
	{
		Intent intent = new Intent(NacActiveAlarmService.ACTION_DISMISS_ALARM, null,
			context, NacActiveAlarmService.class);

		return NacIntent.addAlarm(intent, alarm);
	}

	/**
	 * @return An intent that will be used to dismiss the foreground alarm service
	 *     and indicates that NFC was used.
	 */
	public static Intent dismissForegroundServiceWithNfc(Context context, NacAlarm alarm)
	{
		Intent intent = new Intent(NacActiveAlarmService.ACTION_DISMISS_ALARM_WITH_NFC,
			null, context, NacActiveAlarmService.class);

		return NacIntent.addAlarm(intent, alarm);
	}

	/**
	 * @return The intent action (never null).
	 */
	public static String getAction(Intent intent)
	{
		String action = (intent != null) ? intent.getAction() : null;
		return (action != null) ? action : "";
	}

	/**
	 * @return The alarm associated with the given Intent.
	 */
	public static NacAlarm getAlarm(Intent intent)
	{
		if (intent == null)
		{
			return null;
		}

		Bundle bundle = NacIntent.getAlarmBundle(intent);
		return NacBundle.getAlarm(bundle);
	}

	/**
	 * @see #getBundle(Intent, String)
	 */
	public static Bundle getAlarmBundle(Intent intent)
	{
		return NacIntent.getBundle(intent, ALARM_BUNDLE_NAME);
	}

	/**
	 * @return The extra data bundle that is part of the intent.
	 */
	public static Bundle getBundle(Intent intent, String name)
	{
		return (intent != null) ? intent.getBundleExtra(name) : null;
	}

	/**
	 * @return The alarm that was specified using the SET_ALARM action.
	 */
	public static NacAlarm getSetAlarm(Context context, Intent intent)
	{
		if (!NacIntent.isSetAlarmAction(intent))
		{
			return null;
		}

		NacSharedPreferences shared = new NacSharedPreferences(context);
		NacSharedDefaults defaults = shared.getDefaults();
		NacAlarm.Builder builder = new NacAlarm.Builder(shared);
		Calendar calendar = Calendar.getInstance();
		boolean isSet = false;

		if (intent.hasExtra(AlarmClock.EXTRA_HOUR))
		{
			int hour = intent.getIntExtra(AlarmClock.EXTRA_HOUR,
				calendar.get(Calendar.HOUR_OF_DAY));
			isSet = true;

			builder.setHour(hour);
		}

		if (intent.hasExtra(AlarmClock.EXTRA_MINUTES))
		{
			int minute = intent.getIntExtra(AlarmClock.EXTRA_MINUTES,
				calendar.get(Calendar.MINUTE));
			isSet = true;

			builder.setMinute(minute);
		}

		if (intent.hasExtra(AlarmClock.EXTRA_MESSAGE))
		{
			String name = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE);
			isSet = true;

			builder.setName(name);
		}

		if (intent.hasExtra(AlarmClock.EXTRA_DAYS))
		{
			ArrayList<Integer> extraDays =
				intent.getIntegerArrayListExtra(AlarmClock.EXTRA_DAYS);
			EnumSet<NacCalendar.Day> days = NacCalendar.Day.none();
			isSet = true;

			for (int d : extraDays)
			{
				days.add(NacCalendar.Days.toWeekDay(d));
			}

			builder.setDays(days);
		}

		if (intent.hasExtra(AlarmClock.EXTRA_RINGTONE))
		{
			String ringtone = intent.getStringExtra(AlarmClock.EXTRA_RINGTONE);
			isSet = true;

			builder.setMedia(context, ringtone);
		}

		if (intent.hasExtra(AlarmClock.EXTRA_VIBRATE))
		{
			boolean vibrate = intent.getBooleanExtra(AlarmClock.EXTRA_VIBRATE,
				defaults.getVibrate());
			isSet = true;

			builder.setVibrate(vibrate);
		}

		//getBooleanExtra(AlarmClock.EXTRA_SKIP_UI);

		return (isSet) ? builder.build() : null;
	}

	/**
	 * @return The sound associated with the given intent.
	 */
	public static String getMedia(Intent intent)
	{
		if (intent == null)
		{
			return null;
		}

		Bundle bundle = NacIntent.getMediaBundle(intent);

		return NacBundle.getMedia(bundle);
	}

	/**
	 * @return The sound bundle.
	 */
	public static Bundle getMediaBundle(Intent intent)
	{
		return NacIntent.getBundle(intent, MEDIA_BUNDLE_NAME);
	}

	/**
	 * @return True if the intent was called from the SET_ALARM action, and
	 *         False otherwise.
	 */
	public static boolean isSetAlarmAction(Intent intent)
	{
		if (intent == null)
		{
			return false;
		}

		String action = intent.getAction();
		return (action != null) && action.equals(AlarmClock.ACTION_SET_ALARM);
	}

	/**
	 * @return An intent that will be used to snooze the foreground alarm service.
	 */
	public static Intent snoozeForegroundService(Context context, NacAlarm alarm)
	{
		Intent intent = new Intent(NacActiveAlarmService.ACTION_SNOOZE_ALARM, null,
			context, NacActiveAlarmService.class);
		return NacIntent.addAlarm(intent, alarm);
	}

	/**
	 * @return An intent that allows you to stop the alarm activity.
	 */
	public static Intent stopAlarmActivity(NacAlarm alarm)
	{
		Intent intent = new Intent(NacActiveAlarmActivity.ACTION_STOP_ACTIVITY);

		return (alarm != null) ? NacIntent.addAlarm(intent, alarm) : intent;
	}

	/**
	 * @return An intent that will be used to stop the foreground alarm service.
	 */
	@SuppressWarnings("unused")
	public static Intent stopForegroundService(Context context, NacAlarm alarm)
	{
		Intent intent = new Intent(NacActiveAlarmService.ACTION_STOP_SERVICE, null,
			context, NacActiveAlarmService.class);
		return NacIntent.addAlarm(intent, alarm);
	}

	/**
	 * @return An intent with a sound.
	 */
	public static Intent toIntent(String media)
	{
		return NacIntent.toIntent(null, null, media);
	}

	/**
	 * Create an intent with an alarm attached in the Extra part of the intent.
	 *
	 * @return An intent.
	 *
	 * @param  context  The application context.
	 * @param  cls      The name of the class for the intent to run.
	 * @param  alarm    The alarm to attach to the intent.
	 */
	public static Intent toIntent(Context context, Class<?> cls, NacAlarm alarm)
	{
		Intent intent = (cls != null) ? new Intent(context, cls)
			: new Intent();
		Bundle bundle = NacBundle.toBundle(alarm);

		intent.putExtra(ALARM_BUNDLE_NAME, bundle);
		return intent;
	}

	/**
	 * Create an intent with a media path attached in the Extra part of the
	 * intent.
	 *
	 * @return An intent.
	 *
	 * @param  context  The application context.
	 * @param  cls      The name of the class for the intent to run.
	 * @param  media    The media path to attach to the intent.
	 */
	public static Intent toIntent(Context context, Class<?> cls, String media)
	{
		Intent intent = (cls != null) ? new Intent(context, cls)
			: new Intent();
		Bundle bundle = NacBundle.toBundle(media);

		intent.putExtra(MEDIA_BUNDLE_NAME, bundle);
		return intent;
	}

}
