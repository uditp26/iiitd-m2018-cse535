package com.example.protagonist26.mreverb;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity_A2_MT18049 extends AppCompatActivity implements Mediator_A2_MT18049, Fragment1_A2_MT18049.OnFragmentInteractionListener, Fragment2_A2_MT18049.OnFragmentInteractionListener {

    static Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__a2__mt18049);

        Fragment1_A2_MT18049 frag1 = new Fragment1_A2_MT18049();
        Fragment2_A2_MT18049 frag2 = new Fragment2_A2_MT18049();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.relay0, frag1, "frg1");
        transaction.add(R.id.relay0, frag2, "frg2");
        transaction.commit();
    }

    @Override
    public void react(String s) {
        Intent intent1 = new Intent(this, Service1_A2_MT18049.class);
        Intent intent2 = new Intent(this, Service2_MT_18049.class);
        if(s.equals("BPlay")){
            Log.i("Button","Play button clicked.");
            if(!flag){
                startService(intent1);
                flag = true;
            }
        }
        else if(s.equals("BStop")){
            Log.i("Button","Stop button clicked.");
            flag = false;
            stopService(intent1);
        }
        else if(s.equals("BConnect")){
            Log.i("Button","Connect button clicked.");
            startService(intent2);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
