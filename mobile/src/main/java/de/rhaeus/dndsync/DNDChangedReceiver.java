package de.rhaeus.dndsync;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class DNDChangedReceiver extends BroadcastReceiver{
    private static final String TAG = "DNDChangedReceiver";
    private static final String DND_SYNC_CAPABILITY_NAME = "dnd_sync";
    private static final String DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int dndState = mNotificationManager.getCurrentInterruptionFilter();
            Log.d(TAG, "Current DND state is: " + dndState);

            new Thread(new Runnable() {
                public void run() {
                    sendSyncRequest(context, dndState);
                }
            }).start();
        }
    }

    private void sendSyncRequest(Context context, int dndState) {
        // https://developer.android.com/training/wearables/data/messages

        // search nodes for sync
        CapabilityInfo capabilityInfo = null;
        try {
            capabilityInfo = Tasks.await(
                    Wearable.getCapabilityClient(context).getCapability(
                            DND_SYNC_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "oh god1", e);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "oh god2", e);
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
                            Wearable.getMessageClient(context).sendMessage(node.getId(), DND_SYNC_MESSAGE_PATH, data);
                    // You can add success and/or failure listeners,
                    // Or you can call Tasks.await() and catch ExecutionException
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
