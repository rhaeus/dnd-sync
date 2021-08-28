package de.rhaeus.dndsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    private static final String DND_SYNC_CAPABILITY_NAME = "dnd_sync";
    private String syncNodeId = null;
    private static final String DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the notification policy access has been granted for the app.
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
//            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//            startActivity(intent);
//        }

        // detect capable nodes as they connect
        // This example uses a Java 8 Lambda. You can use named or anonymous classes.
        CapabilityClient.OnCapabilityChangedListener capabilityListener =
                capInfo -> { Log.d(TAG, "updateDndSyncCapability triggered by listener"); updateDndSyncCapability(capInfo); };
        Wearable.getCapabilityClient(getApplicationContext()).addListener(
                capabilityListener,
                DND_SYNC_CAPABILITY_NAME);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onClick event ");

//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
//                int fil = mNotificationManager.getCurrentInterruptionFilter();
//                Toast.makeText(getApplicationContext(), " current filter " + fil, Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    public void run() {
                        // a potentially time consuming task
                        Log.d(TAG, "all Nodes: " + getNodes());
                        sendSyncRequest();
                    }
                }).start();

            }
        });
    }

    private Collection<String> getNodes() {
        HashSet <String>results = new HashSet<String>();
        List<Node> nodes =
                null;
        try {
            nodes = Tasks.await(Wearable.getNodeClient(getApplicationContext()).getConnectedNodes());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert nodes != null;
        for (Node node : nodes) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendSyncRequestAllNodes() {
        // https://developer.android.com/training/wearables/data/messages

        // search node for sync
        CapabilityInfo capabilityInfo = null;
        try {
            capabilityInfo = Tasks.await(
                    Wearable.getCapabilityClient(getApplicationContext()).getCapability(
                            DND_SYNC_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // capabilityInfo has the reachable nodes with the dnd sync capability
        updateDndSyncCapability(capabilityInfo);

        // detect capable nodes as they connect
        // This example uses a Java 8 Lambda. You can use named or anonymous classes.
        CapabilityClient.OnCapabilityChangedListener capabilityListener =
                capInfo -> { updateDndSyncCapability(capInfo); };
        Wearable.getCapabilityClient(getApplicationContext()).addListener(
                capabilityListener,
                DND_SYNC_CAPABILITY_NAME);

        // send request
        if (syncNodeId != null) {
            byte[] data = "syncdnd".getBytes();
            Task<Integer> sendTask =
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            syncNodeId, DND_SYNC_MESSAGE_PATH, data);
            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Toast.makeText(getApplicationContext(), " send successful! node id " + syncNodeId, Toast.LENGTH_LONG).show();
                }
            });
            sendTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), " send failed!", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            // Unable to retrieve node with transcription capability
            Toast.makeText(getApplicationContext(), " Unable to retrieve node with sync capabilityc!", Toast.LENGTH_LONG).show();
        }

    }

    private void sendSyncRequest() {
        // https://developer.android.com/training/wearables/data/messages

        // search node for sync
        CapabilityInfo capabilityInfo = null;
        try {
            capabilityInfo = Tasks.await(
                    Wearable.getCapabilityClient(getApplicationContext()).getCapability(
                            DND_SYNC_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "oh god", e);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "oh god", e);
            return;
        }
        // capabilityInfo has the reachable nodes with the dnd sync capability
        Log.d(TAG, "updateDndSyncCapability triggered by button");
        updateDndSyncCapability(capabilityInfo);

//        // detect capable nodes as they connect
//        // This example uses a Java 8 Lambda. You can use named or anonymous classes.
//        CapabilityClient.OnCapabilityChangedListener capabilityListener =
//                capInfo -> { updateDndSyncCapability(capInfo); };
//        Wearable.getCapabilityClient(getApplicationContext()).addListener(
//                capabilityListener,
//                DND_SYNC_CAPABILITY_NAME);

        // send request
        if (syncNodeId != null) {
            byte[] data = "syncdnd".getBytes();
            Task<Integer> sendTask =
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(
                            syncNodeId, DND_SYNC_MESSAGE_PATH, data);
            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Toast.makeText(getApplicationContext(), " send successful! node id " + syncNodeId, Toast.LENGTH_LONG).show();
                }
            });
            sendTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(), " send failed!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "send failed!");
                }
            });

        } else {
            // Unable to retrieve node with transcription capability
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), " Unable to retrieve node with sync capability!", Toast.LENGTH_LONG).show();
                }
            });

            Log.d(TAG, "Unable to retrieve node with sync capability!");
        }

    }

    private void updateDndSyncCapability(CapabilityInfo capabilityInfo) {

        Set<Node> connectedNodes = capabilityInfo.getNodes();

        syncNodeId = pickBestNodeId(connectedNodes);
        Log.d(TAG, "updateDndSyncCapability. syncNodeId = " + syncNodeId);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }
}
