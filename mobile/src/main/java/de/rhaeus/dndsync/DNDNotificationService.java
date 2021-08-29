package de.rhaeus.dndsync;


import android.service.notification.NotificationListenerService;
import android.util.Log;

import androidx.annotation.NonNull;

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

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "listener connected");
    }

    @Override
    public void onInterruptionFilterChanged (int interruptionFilter) {
        Log.d(TAG, "interruption filter changed to " + interruptionFilter);
        new Thread(new Runnable() {
            public void run() {
                sendDNDSync(interruptionFilter);
            }
        }).start();
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
                    data[0] = 1; // bedtime mode
                    data[1] = (byte) dndState;
                    Task<Integer> sendTask =
                            Wearable.getMessageClient(this).sendMessage(node.getId(), DND_SYNC_MESSAGE_PATH, data);

                    sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            Log.d(TAG, "send successful!");
                        }
                    });

                    sendTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "send failed!");
                        }
                    });
                }
            }
        }
    }
}
