package com.example.protagonist26.mreverb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BR1_A2_MT18049 extends BroadcastReceiver {
    Toast t;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast","Boot Completed");
        t = Toast.makeText(context, "Boot Completed", Toast.LENGTH_LONG);
        t.show();
    }
}
