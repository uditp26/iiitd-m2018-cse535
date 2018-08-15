package com.example.protagonist26.homeassignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayDetails_A1_MT18049 extends AppCompatActivity {

    String s[];
    TextView textView;
    Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details__a1__mt18049);
        Intent intent = getIntent();
        s = intent.getStringArrayExtra("sinfo");
        displayInput();
    }

    protected  void onStart(){
        t = Toast.makeText(getApplicationContext(), "State of Activity Display_Details changed from Created to Started.", Toast.LENGTH_SHORT);
        Log.i("Display_Details","State of Activity Display_Details changed from Created to Started.");
        t.show();
        super.onStart();
    }

    protected void onResume(){
        t = Toast.makeText(getApplicationContext(), "State of Activity Display_Details changed from Started to Resumed.", Toast.LENGTH_SHORT);
        Log.i("Display_Details","State of Activity Display_Details changed from Started to Resumed.");
        t.show();
        super.onResume();
    }

    protected void onPause(){
        t = Toast.makeText(getApplicationContext(), "State of Activity Display_Details changed from Resumed to Paused.", Toast.LENGTH_SHORT);
        Log.i("Display_Details","State of Activity Display_Details changed from Resumed to Paused.");
        t.show();
        super.onPause();
    }

    protected void onStop(){
        t = Toast.makeText(getApplicationContext(), "State of Activity Display_Details changed from Paused to Stopped.", Toast.LENGTH_SHORT);
        Log.i("Display_Details","State of Activity Display_Details changed from Paused to Stopped.");
        t.show();
        super.onStop();
    }

    protected void onRestart(){
        t = Toast.makeText(getApplicationContext(), "State of Activity Display_Details changed from Stopped to Restarted.", Toast.LENGTH_SHORT);
        Log.i("Display_Details","State of Activity Display_Details changed from Stopped to Restarted.");
        t.show();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        t = Toast.makeText(getApplicationContext(), "State of Activity Display_Details changed from Stopped to Destroyed.", Toast.LENGTH_SHORT);
        Log.i("Display_Details","State of Activity Display_Details changed from Stopped to Destroyed.");
        t.show();
        super.onDestroy();
    }

    public void displayInput(){
        textView = findViewById(R.id.op1);
        textView.setText(s[0]);
        textView = findViewById(R.id.op2);
        textView.setText(s[1]);
        textView = findViewById(R.id.op3);
        textView.setText(s[2]);
        textView = findViewById(R.id.op4);
        textView.setText(s[3]);
        textView = findViewById(R.id.op5);
        textView.setText(s[4]);
        textView = findViewById(R.id.op6);
        textView.setText(s[5]);
        textView = findViewById(R.id.op7);
        textView.setText(s[6]);

    }
}

