package com.example.protagonist26.mreverb;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import java.io.File;

public class Service1_A2_MT18049 extends Service {
    MediaPlayer mp;
    //Boolean flag = false;
    public Service1_A2_MT18049() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp = MediaPlayer.create(this, R.raw.bnha);
        playsong(mp);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        stopsong(mp);
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    public void playsong(MediaPlayer mediaPlayer){
        //if(!flag)
            mediaPlayer.start();
    }

    public void stopsong(MediaPlayer mediaPlayer){
        mediaPlayer.stop();
        //flag = true;
    }
}
