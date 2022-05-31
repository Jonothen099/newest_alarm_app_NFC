package com.nfcalarmclock.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;

import com.nfcalarmclock.shared.NacSharedKeys;
import com.nfcalarmclock.shared.NacSharedPreferences;

/**
 * Settings fragment.
 */
public abstract class NacSettingsFragment
	extends PreferenceFragmentCompat
	implements SharedPreferences.OnSharedPreferenceChangeListener
{

	/**
	 * Shared preference store.
	 */
	private NacSharedPreferences mShared;

	/**
	 * @return The shared preference keys.
	 */
	protected NacSharedKeys getSharedKeys()
	{
		NacSharedPreferences shared = this.getSharedPreferences();

		return (shared != null) ? shared.getKeys() : null;
	}

	/**
	 * @return The shared preferences object.
	 */
	protected NacSharedPreferences getSharedPreferences()
	{
		return this.mShared;
	}

	/**
	 */
	@Override
	public void onAttach(@NonNull Context context)
	{
		super.onAttach(context);

		this.mShared = new NacSharedPreferences(context);
	}

	/**
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);
	}

	/**
	 */
	@Override
	public void onPause()
	{
		super.onPause();

		getPreferenceScreen().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
	}

	/**
	 */
	@Override
	public void onSharedPreferenceChanged(
		SharedPreferences sharedPreferences, String preferenceKey)
	{
	}

}
