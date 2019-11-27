package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public class SettingsActivity extends Activity {

    public static final String USERNAME_KEY = Settings.CHAT_NAME_KEY;

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i("PREFS", getPreferenceManager().getSharedPreferencesName());
            // Load the preferences from an XML resource
            getPreferenceManager().setSharedPreferencesName(Settings.SETTINGS);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the messages content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}