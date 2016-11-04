package com.hyphenate.chatuidemo.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.R;

/**
 * Cretae by lzan13 2016/10/26
 * Setting preference
 */
public class SettingsPreference extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference mAboutVersionPreference;
    private EditTextPreference mDisplayNamePreference;

    private String aboutKey;
    private String displayNameKey;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_settings);

        aboutKey = getActivity().getString(R.string.em_pref_key_about);
        displayNameKey = getActivity().getString(R.string.em_pref_key_notification_display_name);

        mAboutVersionPreference = getPreferenceScreen().findPreference(aboutKey);
        mDisplayNamePreference =
                (EditTextPreference) getPreferenceScreen().findPreference(displayNameKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(aboutKey)) {
            checkVersion();
        } else if (key.equals(displayNameKey)) {
            checkDisplayName();
        }
    }

    /**
     * check version
     */
    private void checkVersion() {
        mAboutVersionPreference.setSummary(EMClient.getInstance().VERSION);
    }

    /**
     * check display name preference content
     */
    private void checkDisplayName() {
        if (mDisplayNamePreference.getText() != null && mDisplayNamePreference.getText()
                .equals("")) {
            mDisplayNamePreference.setSummary("Input display name");
        } else {
            mDisplayNamePreference.setSummary(mDisplayNamePreference.getText());
        }
    }

    @Override public void onResume() {
        super.onResume();
        checkVersion();
        checkDisplayName();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override public void onPause() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}