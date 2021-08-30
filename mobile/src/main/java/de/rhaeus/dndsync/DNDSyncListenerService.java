package de.rhaeus.dndsync;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DNDSyncListenerService extends WearableListenerService {
    private static final String TAG = "DNDSyncListenerService";
    private static final String DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync";


    @Override
    public void onMessageReceived (@NonNull MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);

        if (messageEvent.getPath().equalsIgnoreCase(DND_SYNC_MESSAGE_PATH)) {
            Log.d(TAG, "received path: " + DND_SYNC_MESSAGE_PATH);

            byte[] data = messageEvent.getData();
            // data[0] contains dnd mode of phone
            // 0 = INTERRUPTION_FILTER_UNKNOWN
            // 1 = INTERRUPTION_FILTER_ALL (all notifications pass)
            // 2 = INTERRUPTION_FILTER_PRIORITY
            // 3 = INTERRUPTION_FILTER_NONE (no notification passes)
            // 4 = INTERRUPTION_FILTER_ALARMS
            byte dndStatePhone = data[0];
            Log.d(TAG, "dndStatePhone: " + dndStatePhone);

            // get dnd state
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int filterState = mNotificationManager.getCurrentInterruptionFilter();
            if (filterState < 0 || filterState > 4) {
                Log.d(TAG, "DNDSync weird current dnd state: " + filterState);
            }
            byte currentDndState = (byte) filterState;
            Log.d(TAG, "currentDndState: " + currentDndState);

            if (dndStatePhone != currentDndState) {
                Log.d(TAG, "dndStatePhone != currentDndState: " + dndStatePhone + " != " + currentDndState);
                if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                    mNotificationManager.setInterruptionFilter(dndStatePhone);
                    Log.d(TAG, "DND set to " + dndStatePhone);
                } else {
                Log.d(TAG, "attempting to set DND but access not granted");
                }
            }

        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
