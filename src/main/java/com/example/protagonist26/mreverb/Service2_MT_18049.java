package com.example.protagonist26.mreverb;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Service2_MT_18049 extends Service {
    ConnectivityManager cmg;
    Boolean flag;
    URL url;

    public Service2_MT_18049() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            cmg = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            flag = checkConnection(cmg);
            if(flag) {
                downloadSong();
            }
        return START_STICKY;
    }

    public boolean checkConnection(ConnectivityManager connectivityManager){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            Toast.makeText(this,"Internet available.",Toast.LENGTH_LONG).show();
            Log.i("Connection","Internet available.");
            Toast.makeText(this,"Beginning download...",Toast.LENGTH_LONG).show();
            Log.i("Download","Beginning download...");
            return true;
        }
        else{
            Toast.makeText(this,"Internet not available.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void downloadSong(){
        String url = "http://faculty.iiitd.ac.in/~mukulika/s1.mp3";
        File file = new File(this.getFilesDir(), "Downloads");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("s1.mp3");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "/s1.mp3");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
