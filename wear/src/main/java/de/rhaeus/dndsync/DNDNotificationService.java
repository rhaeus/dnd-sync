package de.rhaeus.dndsync;


import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DNDNotificationService extends NotificationListenerService {
    private static final String TAG = "DNDNotificationService";
    private static final String DND_SYNC_CAPABILITY_NAME = "dnd_sync";
    private static final String DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync";
    private static final long   DND_SYNC_MESSAGE_DELAY = 500;

    public static boolean running = false;

    private static Thread syncThread = null;

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "listener connected");
        running = true;

        //TODO enable/disable service based on app setting to save battery
//        // We don't want to run a background service so disable and stop it
//        // to avoid running this service in the background
//        disableServiceComponent();
//        Log.i(TAG, "Disabling service");
//
//        try {
//            stopSelf();
//        } catch(SecurityException e) {
//            Log.e(TAG, "Failed to stop service");
//        }
    }
//    private void disableServiceComponent() {
//        PackageManager p = getPackageManager();
//        ComponentName componentName = new ComponentName(this, DNDNotificationService.class);
//        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//    }

    @Override
    public void onListenerDisconnected() {
        Log.d(TAG, "listener disconnected");
        running = false;
    }


    @Override
    public void onInterruptionFilterChanged (int interruptionFilter) {
        Log.d(TAG, "interruption filter changed to " + interruptionFilter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean syncDnd = prefs.getBoolean("dnd_sync_key", true);
        if(syncDnd) {

            // in case we receive another update in a short time, abort the last one
            if (syncThread != null) {
                syncThread.interrupt();
            }
            syncThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(DND_SYNC_MESSAGE_DELAY);
                    } catch (InterruptedException e) {
                        return;
                    }
                    sendDNDSync(interruptionFilter);
                }
            });
            syncThread.start();
        }
    }

    private void sendDNDSync(int dndState) {
        // https://developer.android.com/training/wearables/data/messages

        // search nodes for sync
        CapabilityInfo capabilityInfo = null;
        try {
            capabilityInfo = Tasks.await(
                    Wearable.getCapabilityClient(this).getCapability(
                            DND_SYNC_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "execution error while searching nodes", e);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "interruption error while searching nodes", e);
            return;
        }

        // send request to all reachable nodes
        // capabilityInfo has the reachable nodes with the dnd sync capability
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        if (connectedNodes.isEmpty()) {
            // Unable to retrieve node with transcription capability
            Log.d(TAG, "Unable to retrieve node with sync capability!");
        } else {
            for (Node node : connectedNodes) {
                if (node.isNearby()) {
                    byte[] data = new byte[2];
                    data[0] = (byte) dndState;
                    Task<Integer> sendTask =
                            Wearable.getMessageClient(this).sendMessage(node.getId(), DND_SYNC_MESSAGE_PATH, data);

                    sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            Log.d(TAG, "send successful! Receiver node id: " + node.getId());
                        }
                    });

                    sendTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "send failed! Receiver node id: " + node.getId());
                        }
                    });
                }
            }
        }
    }
}
