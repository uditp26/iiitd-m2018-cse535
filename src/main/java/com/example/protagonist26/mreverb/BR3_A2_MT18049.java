package com.example.protagonist26.mreverb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BR3_A2_MT18049 extends BroadcastReceiver {
    Toast t;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast","Airplane Mode");
        t = Toast.makeText(context, "Airplane Mode", Toast.LENGTH_LONG);
        t.show();
    }
}
