package com.example.protagonist26.homeassignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity_A1_MT18049 extends AppCompatActivity implements View.OnClickListener{

    EditText et;
    Toast t1;
    public static final String ACTIVITY_NAME = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__a1__mt18049);
    }

    @Override
    public void onClick(View view){

    }

    protected void onStart(){
        t1 = Toast.makeText(getApplicationContext(), "State of Activity MainActivity changed from Created to Started.", Toast.LENGTH_SHORT);
        Log.i(ACTIVITY_NAME,"State of Activity MainActivity changed from Created to Started.");
        t1.show();
        super.onStart();
    }

    protected void onResume(){
        t1 = Toast.makeText(getApplicationContext(), "State of Activity MainActivity changed from Started to Resumed.", Toast.LENGTH_SHORT);
        Log.i(ACTIVITY_NAME,"State of Activity MainActivity changed from Started to Resumed.");
        t1.show();
        super.onResume();
    }

    protected void onPause(){
        t1 = Toast.makeText(getApplicationContext(), "State of Activity MainActivity changed from Resumed to Paused.", Toast.LENGTH_SHORT);
        Log.i(ACTIVITY_NAME,"State of Activity MainActivity changed from Resumed to Paused.");
        t1.show();
        super.onPause();
    }

    protected void onStop(){
        t1 = Toast.makeText(getApplicationContext(), "State of Activity MainActivity changed from Paused to Stopped.", Toast.LENGTH_SHORT);
        Log.i(ACTIVITY_NAME,"State of Activity MainActivity changed from Paused to Stopped.");
        t1.show();
        super.onStop();
    }

    protected void onRestart(){
        t1 = Toast.makeText(getApplicationContext(), "State of Activity MainActivity changed from Stopped to Restarted.", Toast.LENGTH_SHORT);
        Log.i(ACTIVITY_NAME,"State of Activity MainActivity changed from Stopped to Restarted.");
        t1.show();
        super.onRestart();
    }

    protected void onDestroy() {
        t1 = Toast.makeText(getApplicationContext(), "State of Activity MainActivity changed from Stopped to Destroyed.", Toast.LENGTH_SHORT);
        Log.i(ACTIVITY_NAME,"State of Activity MainActivity changed from Stopped to Destroyed.");
        t1.show();
        super.onDestroy();
    }

    public void clearFields(View view){
        et = findViewById(R.id.uname);
        et.setText("");
        et = findViewById(R.id.rno);
        et.setText("");
        et = findViewById(R.id.bname);
        et.setText("");
        et = findViewById(R.id.cname1);
        et.setText("");
        et = findViewById(R.id.cname2);
        et.setText("");
        et = findViewById(R.id.cname3);
        et.setText("");
        et = findViewById(R.id.cname4);
        et.setText("");
        t1 = Toast.makeText(getApplicationContext(), "Fields Cleared!", Toast.LENGTH_SHORT);
        t1.show();
    }

    public void submitDetails(View view){
        String s[] = new String[7];
        et = findViewById(R.id.uname);
        s[0] = et.getText().toString();
        et = findViewById(R.id.rno);
        s[1] = et.getText().toString();
        et = findViewById(R.id.bname);
        s[2] = et.getText().toString();
        et = findViewById(R.id.cname1);
        s[3] = et.getText().toString();
        et = findViewById(R.id.cname2);
        s[4] = et.getText().toString();
        et = findViewById(R.id.cname3);
        s[5] = et.getText().toString();
        et = findViewById(R.id.cname4);
        s[6] = et.getText().toString();
        if(!(s[0].equals("")) && !(s[1].equals("")) && !(s[2].equals("")) && !(s[3].equals("")) && !(s[4].equals("")) && !(s[5].equals("")) && !(s[6].equals(""))){
            Intent intent = new Intent(this, DisplayDetails_A1_MT18049.class);
            intent.setType("text/*");
            intent.putExtra("sinfo", s);
            startActivity(intent);
            t1 = Toast.makeText(getApplicationContext(), "Details submitted.", Toast.LENGTH_SHORT);
            t1.show();
        }
        else{
            t1 = Toast.makeText(getApplicationContext(), "Some fields are missing.", Toast.LENGTH_SHORT);
            t1.show();
        }
    }

}

