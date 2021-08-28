package de.rhaeus.dndsync;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

// https://stackoverflow.com/questions/27185609/android-wear-listen-to-incoming-notifications/38794028#38794028
// https://github.com/blunden/DoNotDisturbSync/blob/master/wear/src/main/java/se/blunden/donotdisturbsync/DummyNotificationListener.java

/**
 * A dummy {@link NotificationListenerService} service that disables and stops itself.
 *
 * Its purpose is to allow users to add the app as a notification listener which
 * automatically grants and enables android.permission.ACCESS_NOTIFICATION_POLICY
 * that can't be enabled the normal way on the watch.
 */
public class DummyNotificationListener extends NotificationListenerService {
    private static final String TAG = "DndDummyService";

    @Override
    public void onListenerConnected() {
        // We don't want to run a background service so disable and stop it
        // to avoid running this service in the background
        disableServiceComponent();
        Log.i(TAG, "Disabling service");

        try {
            stopSelf();
        } catch(SecurityException e) {
            Log.e(TAG, "Failed to stop service");
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Override this method to be explicit that we don't eavesdrop on notifications
    }

    private void disableServiceComponent() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, DummyNotificationListener.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}