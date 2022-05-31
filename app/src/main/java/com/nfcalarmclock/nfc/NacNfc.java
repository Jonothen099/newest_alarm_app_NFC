package com.nfcalarmclock.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;

import com.nfcalarmclock.alarm.NacAlarm;
import com.nfcalarmclock.shared.NacSharedConstants;
import com.nfcalarmclock.util.NacUtility;

import java.lang.IllegalStateException;
import java.lang.SecurityException;

/**
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class NacNfc
{

	/**
	 * Add an NFC tag into an Intent.
	 */
	public static void addTagToIntent(Intent intent, Tag tag)
	{
		if ((intent == null) || (tag == null))
		{
			return;
		}

		intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
	}

	/**
	 * Check the saved NFC tag ID in the alarm against the one in the intent.
	 * Return True if the alarm does not have a saved ID, or if the IDs match,
	 * and False otherwise.
	 */
	public static boolean doIdsMatch(NacAlarm alarm, Intent intent)
	{
		if ((alarm == null) || (intent == null))
		{
			return false;
		}

		Tag nfcTag = NacNfc.getTag(intent);
		if (nfcTag == null)
		{
			return false;
		}

		String nfcId = NacNfc.parseId(nfcTag);
		String alarmId = alarm.getNfcTagId();
		return alarmId.isEmpty() || alarmId.equals(nfcId);
	}

	/**
	 * Check if NFC exists on this device.
	 */
	public static boolean exists(Context context)
	{
		return (NfcAdapter.getDefaultAdapter(context) != null);
	}

	/**
	 * @return An NFC tag from the given Intent.
	 */
	public static Tag getTag(Intent intent)
	{
		return (intent != null)
			? (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
			: null;
	}

	/**
	 * @return True if the NFC adapter is enabled, and False otherwise.
	 */
	public static boolean isEnabled(Context context)
	{
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
		return (nfcAdapter != null) && nfcAdapter.isEnabled();
	}

	/**
	 * Parse NFC tag ID to a readable format.
	 */
	public static String parseId(Tag nfcTag)
	{
		// NFC tag is not defined
		if (nfcTag == null)
		{
			return null;
		}

		byte[] srcId = nfcTag.getId();

		// Unable to find an ID on the NFC tag
		if (srcId == null)
		{
			return "";
		}

		StringBuilder id = new StringBuilder();
		char[] buffer = new char[2];

		// Compile the NFC tag ID
		for (byte b : srcId)
		{
			buffer[0] = Character.forDigit((b >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(b & 0x0F, 16);
			id.append(buffer);
		}

		return id.toString();
	}

	/**
	 * @see #parseId(Tag)
	 */
	public static String parseId(Intent intent)
	{
		Tag nfcTag = NacNfc.getTag(intent);

		return (nfcTag != null) ? NacNfc.parseId(nfcTag) : null;
	}

	/**
	 * Prompt the user to enable NFC.
	 */
	public static void prompt(Context context)
	{
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

		if (nfcAdapter == null)
		{
			return;
		}

		NacSharedConstants cons = new NacSharedConstants(context);
		Intent settings = new Intent(Settings.ACTION_NFC_SETTINGS);

		NacUtility.toast(context, cons.getMessageNfcRequest());
		context.startActivity(settings);
	}

	/**
	 * @return True if should use NFC, and False otherwise.
	 */
	public static boolean shouldUseNfc(Context context, NacAlarm alarm)
	{
		return (alarm != null) && NacNfc.exists(context) && alarm.shouldUseNfc();
	}

	/**
	 * @see #start(Activity, Intent)
	 */
	public static void start(Activity activity)
	{
		Intent intent = new Intent(activity, activity.getClass())
			.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		NacNfc.start(activity, intent);
	}

	/**
	 * Enable NFC dispatch, so that the app can discover NFC tags.
	 */
	public static void start(Activity activity, Intent intent)
	{
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);

		// NFC adapter is not present or not enabled
		if ((nfcAdapter == null) || !NacNfc.isEnabled(activity))
		{
			return;
		}

		// Determine the pending intent flags
		int flags = 0;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
		{
			flags |= PendingIntent.FLAG_MUTABLE;
		}

		// Create the pending intent
		PendingIntent pending = PendingIntent.getActivity(activity, 0, intent, flags);

		// Enable NFC foreground dispatch
		try
		{
			nfcAdapter.enableForegroundDispatch(activity, pending, null, null);
		}
		catch (SecurityException e)
		{
			NacUtility.quickToast((Context)activity, "Unable to scan NFC tags");
		}
	}

	/**
	 * Stop NFC dispatch, so the app does not waste battery when it does not
	 * need to discover NFC tags.
	 */
	public static void stop(Context context)
	{
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

		if ((nfcAdapter == null) || !NacNfc.isEnabled(context))
		{
			return;
		}

		try
		{
			nfcAdapter.disableForegroundDispatch((Activity)context);
		}
		catch (IllegalStateException e)
		{
		}
	}

	/**
	 * @return True if an NFC tag was scanned/discovered and False otherwise.
	 */
	public static boolean wasScanned(Intent intent)
	{
		if (intent == null)
		{
			return false;
		}

		String action = intent.getAction();
		if ((action == null) || action.isEmpty())
		{
			return false;
		}

		return action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)
			|| action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)
			|| action.equals(NfcAdapter.ACTION_TAG_DISCOVERED);
	}

}
