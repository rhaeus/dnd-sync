package de.rhaeus.dndsync;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DNDSyncListenerService extends WearableListenerService {
    private static final String TAG = "DNDSyncListenerService";
    private static final String DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync";

    @Override
    public void onMessageReceived (MessageEvent messageEvent) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onMessageReceived: " + messageEvent);
        }

        if (messageEvent.getPath().equalsIgnoreCase(DND_SYNC_MESSAGE_PATH)) {

            byte[] data = messageEvent.getData();
            // data[0] contains if dnd or bedtime mode
            // 0 = dnd
            // 1 = bedtime mode
            // data[1] contains dnd mode of phone
            // 0 = INTERRUPTION_FILTER_UNKNOWN
            // 1 = INTERRUPTION_FILTER_ALL (all notifications pass)
            // 2 = INTERRUPTION_FILTER_PRIORITY
            // 3 = INTERRUPTION_FILTER_NONE (no notification passes)
            // 4 = INTERRUPTION_FILTER_ALARMS
            byte mode = data[0];
            Log.d(TAG, "mode: " + mode);
            byte dndStatePhone = data[1];
            Log.d(TAG, "dndStatePhone: " + dndStatePhone);

            // get dnd state
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
//                Toast.makeText(getApplicationContext(), "DNDSync missing DND permission!", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "DNDSync missing DND permission!");
//            }

            int filterState = mNotificationManager.getCurrentInterruptionFilter();
            if (filterState < 0 || filterState > 4) {
                Log.d(TAG, "DNDSync weird current dnd state: " + filterState);
            }
            byte currentDndState = (byte) filterState;
            Log.d(TAG, "currentDndState: " + currentDndState);

            if (mode == 0) { //dnd
                if (dndStatePhone != currentDndState) {
                    mNotificationManager.setInterruptionFilter(dndStatePhone);
//                    Toast.makeText(getApplicationContext(), "DND set to " + dndStatePhone, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "DND set to " + dndStatePhone);
                }
            } else {
                Log.d(TAG, "DNDSync mode went wrong: " + mode);
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
