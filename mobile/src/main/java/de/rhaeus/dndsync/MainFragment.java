package de.rhaeus.dndsync;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class MainFragment extends PreferenceFragmentCompat {
    private Preference dndPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        dndPref = findPreference("dnd_permission_key");

        assert(dndPref != null);

        dndPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (!checkDNDPermission()) {
                    openDNDPermissionRequest();
                } else {
                    Toast.makeText(getContext(), "DND Permission allowed", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        checkDNDPermission();
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

    private void openDNDPermissionRequest() {
       Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
       startActivity(intent);
    }
}