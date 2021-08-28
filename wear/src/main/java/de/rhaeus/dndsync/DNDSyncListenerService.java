package de.rhaeus.dndsync;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
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
//        Toast.makeText(getApplicationContext(), "onMessageReceived!", Toast.LENGTH_LONG).show();

        if (messageEvent.getPath().equalsIgnoreCase(DND_SYNC_MESSAGE_PATH)) {
            Toast.makeText(getApplicationContext(), "onMessageReceived dnd sync!", Toast.LENGTH_LONG).show();

           // turn on screen
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "dndsync:MyWakeLock");
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP , "dndsync:MyWakeLock");
            wakeLock.acquire(2*60*1000L /*2 minutes*/);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // enable bedtime mode
            enableBedtimeMode();
            wakeLock.release();

        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void enableBedtimeMode() {
        ExampleAccessService serv = ExampleAccessService.getSharedInstance();
        if (serv == null) {
            Log.d(TAG, "accessibility not connected");
            return;
        }

        serv.swipeDown();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serv.clickBedMode();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serv.goBack();

    }

}
