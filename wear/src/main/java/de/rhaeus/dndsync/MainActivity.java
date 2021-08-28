package de.rhaeus.dndsync;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.rhaeus.dndsync.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;
    private ExampleAccessService serv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serv = ExampleAccessService.getSharedInstance();
                if (serv == null) {
                    Toast.makeText(getApplicationContext(), "blub argh service not connected!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "service connected!", Toast.LENGTH_LONG).show();
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