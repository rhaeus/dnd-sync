package de.rhaeus.dndsync;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.rhaeus.dndsync.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;
    private DNDSyncAccessService serv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button btnDndCheck = (Button) findViewById(R.id.btnDndCheck);
        btnDndCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check if the notification policy access has been granted for the app.
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int fil = mNotificationManager.getCurrentInterruptionFilter();
                Toast.makeText(getApplicationContext(), "DND permission filter: " + fil, Toast.LENGTH_SHORT).show();
//                if (mNotificationManager.isNotificationPolicyAccessGranted()) {
//                    Toast.makeText(getApplicationContext(), "DND permission ok!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "DND permission missing!", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        Button btnAccCheck = (Button) findViewById(R.id.btnAccCheck);
        btnAccCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serv = DNDSyncAccessService.getSharedInstance();
                if (serv == null) {
                    Toast.makeText(getApplicationContext(), "Accessibility service not connected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Accessibility service connected!", Toast.LENGTH_SHORT).show();
                }
            }
        });


//
//        Button button = (Button) findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                if (serv != null) {
//                    serv.swipeDown();
//                } else {
//                    Toast.makeText(getApplicationContext(), "blub argh service not connected!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//////                Toast.makeText(getApplicationContext(), "This is my Toast message!", Toast.LENGTH_LONG).show();
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (serv != null) {
//                    serv.clickBedMode();
//                } else {
//                    Toast.makeText(getApplicationContext(), "blub argh service not connected!", Toast.LENGTH_LONG).show();
//                    return;
//                }
////
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (serv != null) {
//                    serv.goBack();
//                } else {
//                    Toast.makeText(getApplicationContext(), "blub argh service not connected!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//            }
//        });
    }

}