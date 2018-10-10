package com.quizup.app;
//locally hosted at http://quizserver.com [http://192.168.65.193:80/quiz-server]
//For sending data to the server and invoking php script, help has been taken from external sources (StackOverflow).

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements QList.OnFragmentInteractionListener, Detail.OnFragmentInteractionListener, Communicator{

    FragmentManager manager;
    FragmentTransaction transaction;
    QList frag1;
    Detail frag2;
    Bundle bundle;
    DBClass database;
    SQLiteDatabase db;
    String question;
    Cursor cursor;
    QUploader qUploader;
    ConnectivityManager connectivityManager;
    File file;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        frag1 = new QList();
        progress = new ProgressDialog(this);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.relay1, frag1, "frag1");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        database = new DBClass(this);
        db = openOrCreateDatabase("quizinfo", MODE_PRIVATE,null);
        Log.i("CreateDB","Table Version " + db.getVersion());
        if(db.getVersion()==0){
            database.onCreate(db);
        }
        else{
            database.setReference(db);
        }
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        manager.popBackStackImmediate();
        if(manager.getBackStackEntryCount() == 0){
            super.onBackPressed();
        }
        else{
            if(manager.popBackStackImmediate()){
                transaction = manager.beginTransaction();
                transaction.remove(frag2).commit();
                transaction = manager.beginTransaction();
                transaction.add(R.id.relay1, frag1, "frag1").addToBackStack(null).commit();
            }
        }
    }

    @Override
    protected void onStop() {
        if(cursor!=null && db!=null){
            cursor.close();
            db.close();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //this.deleteDatabase("quizinfo");    // <--- answers should be saved
        super.onDestroy();
    }

    @Override
    public void reactor(int pos) {
        try{
            cursor = db.query("DETAILS", new String[]{"Question"}, "Sno=?", new String[]{Integer.toString(pos+1)}, null, null, null);
            Log.i("Cursor","Cursor detail:" + cursor.toString());
            cursor.moveToFirst();
            question = cursor.getString(0);
            bundle = new Bundle();
            bundle.putString("ques", question);
            bundle.putInt("pos", pos);
            transaction = manager.beginTransaction();
            transaction.remove(frag1).commit();
            frag2 = new Detail();
            frag2.setArguments(bundle);
            transaction = manager.beginTransaction();
            transaction.add(R.id.relay1, frag2,"frag2").addToBackStack(null).commit();
        }
        catch (Exception e){
            Log.e("Exception","Exception caught! " + e.toString());
        }

    }

    @Override
    public void updator(int pos, Boolean val) {
        if(val){
            database.insertRecord(pos, "TRUE");
        }
        else{
            database.insertRecord(pos, "FALSE");
        }
    }

    @Override
    public void exporter() {
        file = database.exporttoCSV();
        if(file != null){
            connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            qUploader = new QUploader(connectivityManager, file, this);
            qUploader.execute();
        }
    }

    @Override
    public Boolean checker(int pos) {
        String s = database.checkStatus(pos);
        if(s != null){
            if(s.equals("TRUE")){
                return true;
            }
            else{
                return false;
            }
        }
        else
            return null;
    }

    public class QUploader extends AsyncTask<Void, Integer, Void> {
        private ConnectivityManager connectivityManager;
        File file;
        NetworkInfo info;
        private HttpURLConnection urlConnection;
        private DataOutputStream output = null;
        InputStream input;
        int bytesRead, rem = 1024, bmax = 1024, responseCode, br1, prog = 0, bytesAvailable, bsize;
        byte[] buffer;
        Context context;
        Boolean flag = true;

        public QUploader(ConnectivityManager connectivityManager, File file, Context context){
            this.connectivityManager = connectivityManager;
            this.file = file;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setMessage("Uploading to server...");
            progress.setIndeterminate(false);
            progress.setProgress(0);
            progress.setMax(100);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            prog += values[0];
            if(prog>bmax)
                prog = bmax;
            Double d = Double.valueOf(((double)prog/bmax)*100);
            progress.setProgress(d.intValue());
            Log.i("Progress","Upload %: " + d.intValue());
            rem -= values[0];
        }

        @Override
        protected Void doInBackground(Void... voids) {
            info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
                Log.i("Connection","Network available.");
                //upload to server
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    URL url = new URL("http://192.168.65.193:80/quiz-server/upload.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");
                    urlConnection.setRequestProperty("file", file.getName());
                    try{
                        output = new DataOutputStream(urlConnection.getOutputStream());
                    }
                    catch(ConnectException ce){
                        Log.e("Exception","DataOutputStream: " + ce.toString());
                        flag = false;
                        progress.cancel();
                    }
                    if(flag){
                        output.writeBytes("--" + "*****" + "\r\n");
                        output.writeBytes("Content-Disposition: form-data; name=\"fileToUpload\";filename=\"" + file.getName() + "\"" + "\r\n");
                        output.writeBytes("\r\n");
                        bytesAvailable = fileInputStream.available();
                        bsize = Math.min(bytesAvailable, bmax);
                        buffer = new byte[bsize];
                        bytesRead = fileInputStream.read(buffer, 0, bsize);
                        br1 = bytesRead;
                        Log.i("InputStream","Bytes Read: " + bytesRead);
                        while (bytesRead > 0) {
                            output.write(buffer, 0, bsize);
                            bytesAvailable = fileInputStream.available();
                            bsize = Math.min(bytesAvailable, bmax);
                            bytesRead = fileInputStream.read(buffer, 0, bsize);
                            Log.i("InputStream","Bytes Read: " + bytesRead);
                        }
                        output.writeBytes("\r\n");
                        output.writeBytes("--" + "*****" + "--" + "\r\n");
                        responseCode = urlConnection.getResponseCode();
                        fileInputStream.close();
                        output.flush();
                        output.close();
                        while(rem > 0){
                            publishProgress(br1);
                            Thread.sleep(200);
                            br1 *= 2;
                            //if(br1>bmax)
                            //br1 = bmax;
                        }
                        progress.dismiss();
                        Log.i("Connection","File uploaded to server!");
                        Log.i("Network","Response Code: " + responseCode);
                    }
                }
                catch (Exception e) {
                    Log.e("Connection", e.toString());
                    flag = false;
                    progress.cancel();
                }
                finally {
                    urlConnection.disconnect();
                }
            }
            else{
                Log.i("Network","Connection not available");
                //cancel(true);
                flag = false;
                progress.cancel();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(flag)
                Toast.makeText(context,"File uploaded to the server!",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,"Failed to connect to the server!",Toast.LENGTH_SHORT).show();
        }
    }
}
