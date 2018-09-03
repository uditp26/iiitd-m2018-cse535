package com.example.protagonist26.mreverb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BR2_MT_18049 extends BroadcastReceiver {
    Toast t;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast","Power Connected");
        t = Toast.makeText(context, "Power Connected", Toast.LENGTH_LONG);
        t.show();;
    }
}
