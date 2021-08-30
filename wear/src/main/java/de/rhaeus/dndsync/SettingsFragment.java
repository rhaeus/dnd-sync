package de.rhaeus.dndsync;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference dndPref;
    private Preference accPref;
    private SwitchPreferenceCompat bedtimePref;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        dndPref = findPreference("dnd_permission_key");
        accPref = findPreference("acc_permission_key");
        bedtimePref = (SwitchPreferenceCompat) findPreference("bedtime_key");
//        test = findPreference("test");
//        test.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//
//                return true;
//            }
//        });

        dndPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (!checkDNDPermission()) {
                    Toast.makeText(getContext(), "Follow the instructions to grant the permission via ADB!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        accPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
            if (!checkAccessibilityService()) {
                openAccessibility();
            }
            return true;
            }
        });

        checkDNDPermission();
        checkAccessibilityService();

    }

    private boolean checkAccessibilityService() {
        DNDSyncAccessService serv = DNDSyncAccessService.getSharedInstance();
        boolean connected = serv != null;
        if (connected) {
            accPref.setSummary(R.string.acc_permission_allowed);
            bedtimePref.setEnabled(true);
        } else {
            accPref.setSummary(R.string.acc_permission_not_allowed);
            bedtimePref.setEnabled(false);
            bedtimePref.setChecked(false);
        }
        return connected;
    }

    private boolean checkDNDPermission() {
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        boolean allowed = mNotificationManager.isNotificationPolicyAccessGranted();
        if (allowed) {
            dndPref.setSummary(R.string.dnd_permission_allowed);
        } else {
            dndPref.setSummary(R.string.dnd_permission_not_allowed);
        }
        return allowed;
    }

    private void openAccessibility() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}