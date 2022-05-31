package com.nfcalarmclock.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.nfcalarmclock.audiooptions.NacAlarmAudioOptionsDialog;
import com.nfcalarmclock.audiosource.NacAudioSourceDialog;
import com.nfcalarmclock.autodismiss.NacAutoDismissPreference;
import com.nfcalarmclock.graduallyincreasevolume.NacGraduallyIncreaseVolumeDialog;
import com.nfcalarmclock.maxsnooze.NacMaxSnoozePreference;
import com.nfcalarmclock.mediapicker.NacMediaActivity;
import com.nfcalarmclock.mediapicker.NacMediaPreference;
import com.nfcalarmclock.R;
import com.nfcalarmclock.restrictvolume.NacRestrictVolumeDialog;
import com.nfcalarmclock.shared.NacSharedKeys;
import com.nfcalarmclock.shared.NacSharedPreferences;
import com.nfcalarmclock.snoozeduration.NacSnoozeDurationPreference;
import com.nfcalarmclock.system.NacIntent;
import com.nfcalarmclock.tts.NacTextToSpeechDialog;
import com.nfcalarmclock.volume.NacVolumePreference;

/**
 * General settings fragment.
 */
public class NacGeneralSettingsFragment
	extends NacSettingsFragment
	implements Preference.OnPreferenceClickListener,
		ActivityResultCallback<ActivityResult>,
		NacVolumePreference.OnAudioOptionsClickedListener,
		NacAlarmAudioOptionsDialog.OnAudioOptionClickedListener,
		NacAudioSourceDialog.OnAudioSourceSelectedListener,
		NacGraduallyIncreaseVolumeDialog.OnGraduallyIncreaseVolumeListener,
		NacRestrictVolumeDialog.OnRestrictVolumeListener,
		NacTextToSpeechDialog.OnTextToSpeechOptionsSelectedListener
{

	/**
	 * Activity result launcher, used to get results from a finished activity.
	 */
	private ActivityResultLauncher<Intent> mActivityLauncher;

	/**
	 * The sound preference.
	 */
	private NacMediaPreference mMediaPreference;

	/**
	 * @return The activity result launcher.
	 */
	private ActivityResultLauncher<Intent> getActivityLauncher()
	{
		return this.mActivityLauncher;
	}

	/**
	 * @return The media preference.
	 */
	private NacMediaPreference getMediaPreference()
	{
		return this.mMediaPreference;
	}

	/**
	 * Called when the NacMediaActivity is finished is returns a result.
	 */
	@Override
	public void onActivityResult(ActivityResult result)
	{
		Intent data = result.getData();
		int code = result.getResultCode();

		if (code == Activity.RESULT_OK)
		{
			String media = NacIntent.getMedia(data);
			this.setPreferenceMedia(media);
		}

	}

	/**
	 * Called when an item in the audio options dialog is clicked.
	 */
	@Override
	public void onAudioOptionClicked(long alarmId, int which)
	{
		switch (which)
		{
			case 0:
				this.showAudioSourceDialog();
				break;
			case 1:
				this.showGraduallyIncreaseVolumeDialog();
				break;
			case 2:
				this.showRestrictVolumeDialog();
				break;
			case 3:
				this.showTextToSpeechDialog();
				break;
			default:
				break;
		}
	}

	/**
	 */
	@Override
	public void onAudioOptionsClicked()
	{
		this.showAudioOptionsDialog();
	}

	/**
	 * Called when an audio source is selected.
	 */
	@Override
	public void onAudioSourceSelected(String audioSource)
	{
		NacSharedPreferences shared = this.getSharedPreferences();

		shared.editAudioSource(audioSource);
	}

	/**
	 */
	@Override
	public void onGraduallyIncreaseVolume(boolean shouldIncrease)
	{
		NacSharedPreferences shared = this.getSharedPreferences();

		shared.editShouldGraduallyIncreaseVolume(shouldIncrease);
	}

	/**
	 */
	@Override
	public void onRestrictVolume(boolean shouldRestrict)
	{
		NacSharedPreferences shared = this.getSharedPreferences();

		shared.editShouldRestrictVolume(shouldRestrict);
	}

	/**
	 */
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
		addPreferencesFromResource(R.xml.general_preferences);
		PreferenceManager.setDefaultValues(getContext(), R.xml.general_preferences,
			false);

		NacSharedKeys keys = this.getSharedKeys();

		NacAutoDismissPreference autoDismissPref = findPreference(keys.getAutoDismiss());
		NacMaxSnoozePreference maxSnoozePref = findPreference(keys.getMaxSnooze());
		NacSnoozeDurationPreference snoozeDurationPref = findPreference(keys.getSnoozeDuration());
		NacVolumePreference volumePref = findPreference(keys.getVolume());
		NacMediaPreference mediaPref = findPreference(keys.getMediaPath());
		this.mMediaPreference = mediaPref;
		this.mActivityLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(), this);

		autoDismissPref.setOnPreferenceClickListener(this);
		maxSnoozePref.setOnPreferenceClickListener(this);
		snoozeDurationPref.setOnPreferenceClickListener(this);
		volumePref.setOnAudioOptionsClickedListener(this);
		mediaPref.setOnPreferenceClickListener(this);
	}

	/**
	 */
	@Override
	public boolean onPreferenceClick(Preference pref)
	{
		NacSharedKeys keys = this.getSharedKeys();
		String k = pref.getKey();

		if (k.equals(keys.getAutoDismiss()))
		{
			((NacAutoDismissPreference)pref).showDialog(getChildFragmentManager());
		}
		else if (k.equals(keys.getMaxSnooze()))
		{
			((NacMaxSnoozePreference)pref).showDialog(getChildFragmentManager());
		}
		else if (k.equals(keys.getSnoozeDuration()))
		{
			((NacSnoozeDurationPreference)pref).showDialog(getChildFragmentManager());
		}
		else if (k.equals(keys.getMediaPath()))
		{
			String media = this.getSharedPreferences().getMediaPath();
			Intent intent = NacIntent.toIntent(getContext(), NacMediaActivity.class,
				media);

			this.getActivityLauncher().launch(intent);
		}

		return true;
	}

	/**
	 * Called when a text-to-speech option is selected.
	 */
	@Override
	public void onTextToSpeechOptionsSelected(boolean useTts, int freq)
	{
		NacSharedPreferences shared = this.getSharedPreferences();

		shared.editSpeakToMe(useTts);
		shared.editSpeakFrequency(freq);
	}

	/**
	 * Set the media to be used in the media preference.
	 */
	public void setPreferenceMedia(String media)
	{
		this.getMediaPreference().setMedia(media);
	}

	/**
	 * Show the audio options dialog.
	 */
	public void showAudioOptionsDialog()
	{
		NacAlarmAudioOptionsDialog dialog = new NacAlarmAudioOptionsDialog();

		dialog.setOnAudioOptionClickedListener(this);
		dialog.show(getChildFragmentManager(), NacAlarmAudioOptionsDialog.TAG);
	}

	/**
	 * Show the audio source dialog.
	 */
	public void showAudioSourceDialog()
	{
		NacSharedPreferences shared = this.getSharedPreferences();
		String audioSource = shared.getAudioSource();
		NacAudioSourceDialog dialog = new NacAudioSourceDialog();

		dialog.setDefaultAudioSource(audioSource);
		dialog.setOnAudioSourceSelectedListener(this);
		dialog.show(getChildFragmentManager(), NacAudioSourceDialog.TAG);
	}

	/**
	 * Show the gradually increase volume dialog.
	 */
	public void showGraduallyIncreaseVolumeDialog()
	{
		NacSharedPreferences shared = this.getSharedPreferences();
		NacGraduallyIncreaseVolumeDialog dialog = new NacGraduallyIncreaseVolumeDialog();
		boolean shouldIncrease = shared.getShouldGraduallyIncreaseVolume();

		dialog.setDefaultShouldGraduallyIncreaseVolume(shouldIncrease);
		dialog.setOnGraduallyIncreaseVolumeListener(this);
		dialog.show(getChildFragmentManager(), NacGraduallyIncreaseVolumeDialog.TAG);
	}

	/**
	 * Show the restrict volume dialog.
	 */
	public void showRestrictVolumeDialog()
	{
		NacSharedPreferences shared = this.getSharedPreferences();
		NacRestrictVolumeDialog dialog = new NacRestrictVolumeDialog();
		boolean shouldRestrict = shared.getShouldRestrictVolume();

		dialog.setDefaultShouldRestrictVolume(shouldRestrict);
		dialog.setOnRestrictVolumeListener(this);
		dialog.show(getChildFragmentManager(), NacRestrictVolumeDialog.TAG);
	}

	/**
	 * Show the text-to-speech dialog.
	 */
	public void showTextToSpeechDialog()
	{
		NacSharedPreferences shared = this.getSharedPreferences();
		boolean useTts = shared.getSpeakToMe();
		int freq = shared.getSpeakFrequency();
		NacTextToSpeechDialog dialog = new NacTextToSpeechDialog();

		dialog.setDefaultUseTts(useTts);
		dialog.setDefaultTtsFrequency(freq);
		dialog.setOnTextToSpeechOptionsSelectedListener(this);
		dialog.show(getChildFragmentManager(), NacTextToSpeechDialog.TAG);
	}

}
