package com.appsense.app.appsense;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    SensorManager sensorManager;
    LocationManager locationManager;
    Sensor accelerometer;
    Sensor gyroscope;
    Sensor proximity;
    Sensor orientation;
    TextView x2, y2, z2, x3, y3, z3, x4, y4, z4, x5, y5, z5, x6, y6, z6;
    Button stop;
    SensorDB database;
    SQLiteDatabase sqldb;
    boolean flag, flag2, flag3, flag4;
    double xp, yp, zp, tp, xo, yo, zo, to, vth, vnew;
    int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flag = true;
        flag2 = false;
        flag3 = false;
        c = 0;
        vth = 500;      // <--- threshold velocity in m/s
        x2 = findViewById(R.id.textView4);
        y2 = findViewById(R.id.textView5);
        z2 = findViewById(R.id.textView6);
        x3 = findViewById(R.id.textView7);
        y3 = findViewById(R.id.textView8);
        z3 = findViewById(R.id.textView9);
        x4 = findViewById(R.id.textView10);
        y4 = findViewById(R.id.textView11);
        z4 = findViewById(R.id.textView12);
        x5 = findViewById(R.id.textView13);
        y5 = findViewById(R.id.textView14);
        z5 = findViewById(R.id.textView15);
        x6 = findViewById(R.id.textView16);
        y6 = findViewById(R.id.textView17);
        z6 = findViewById(R.id.textView18);
        stop = findViewById(R.id.button1);
        stop.setOnClickListener(this);
        stop.setText(R.string.slog);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, listener);
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            x5.setText(""+location.getLatitude());
            y5.setText(""+location.getLongitude());
            z5.setText(""+location.getAltitude());
            if(flag){
                database.insertRecord(-1, location.getLatitude(), location.getLongitude(), location.getAltitude(), ""+location.getTime());
                Log.d(TAG, "onLocationChanged: (x,y,z) = (" + location.getLatitude() + ", " + location.getLongitude() + ", "+ location.getAltitude()+ ")");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
        database = new SensorDB(this);
        sqldb = openOrCreateDatabase("sensordb", MODE_PRIVATE, null);
        Log.d(TAG, "onResume: DBVersion " + sqldb.getVersion());
        if(sqldb.getVersion()==0){
            database.onCreate(sqldb);
        }
        else{
            database.initDB(sqldb);
            Log.d(TAG, "onResume: Db already exists. " + sqldb.getVersion());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(sqldb != null){
            sqldb.close();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            x2.setText(""+event.values[0]);
            y2.setText(""+event.values[1]);
            z2.setText(""+event.values[2]);
            if(flag){
                database.insertRecord(Sensor.TYPE_ACCELEROMETER, event.values[0], event.values[1], event.values[2], ""+event.timestamp);
                Log.d(TAG, "ACCELEROMETER: x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);
            }
            if(detectShake(event.values[0], event.values[1], event.values[2], Math.floor(event.timestamp/1000000))){    // <--- passing timestamp in milliseconds
                Toast.makeText(getApplicationContext(), "Shake detected!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Shake Detected!");
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            x3.setText(""+event.values[0]);
            y3.setText(""+event.values[1]);
            z3.setText(""+event.values[2]);
            if(flag){
                database.insertRecord(Sensor.TYPE_GYROSCOPE, event.values[0], event.values[1], event.values[2], ""+event.timestamp);
                Log.d(TAG, "GYROSCOPE: x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);
            }

        }
        else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            x6.setText(""+event.values[0]);
            y6.setText("-");
            z6.setText("-");
            if(flag){
                database.insertRecord(Sensor.TYPE_PROXIMITY, event.values[0], event.values[1], event.values[2], ""+event.timestamp);
                Log.d(TAG, "PROXIMITY: x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);
            }
            if(event.values[0] <= 0.5){
                flag3=true;
                //Log.d(TAG, "onSensorChanged: Proximity Changed -> less than 0.5");
            }
            else{
                flag3=false;
                //Log.d(TAG, "onSensorChanged: Proximity Changed -> greater than 0.5");
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            x4.setText(""+event.values[0]);
            y4.setText(""+event.values[1]);
            z4.setText(""+event.values[2]);
            if(flag){
                database.insertRecord(Sensor.TYPE_ORIENTATION, event.values[0], event.values[1], event.values[2], ""+event.timestamp);
                Log.d(TAG, "ORIENTATION: x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);
            }
            if(flag3){
                detectOrientation(event.values[0], event.values[1], event.values[2], Math.floor(event.timestamp/1000000));
            }
        }
    }

    private boolean detectShake(double x, double y, double z, double t){
        if(!flag2){
            flag2 = true;
        }
        else{
            if(!(xp == x && yp == y && zp == z)){
                vnew = Math.abs(xp-x)*(t-tp) + Math.abs(yp-y)*(t-tp) + Math.abs(zp-z)*(t-tp);
                Log.d(TAG, "detectShake: Velocity: " + vnew);
                if(vnew>vth){
                    xp = x;
                    yp = y;
                    zp = z;
                    tp = t;
                    return true;
                }
            }
        }
        xp = x;
        yp = y;
        zp = z;
        tp = t;
        return false;
    }

    private void detectOrientation(double x, double y, double z, double t){
        if(!flag4){
            xo = x;
            yo = y;
            zo = z;
            to = t;
            flag4 = true;
        }
        else {
            //Log.d(TAG, "detectOrientation: " + Math.round(Math.abs(z-zo)));
            if(Math.round(Math.abs(z-zo)) == 0 && c!=0){
                Log.d(TAG, "detectOrientation: ROTATION_0");
                Toast.makeText(this, "Screen rotated!", Toast.LENGTH_SHORT).show();
                c = 0;
            }
            else if(Math.round(Math.abs(z-zo)) == 2 && c!=2){
                Log.d(TAG, "detectOrientation: ROTATION_90/ROTATION_270");
                Toast.makeText(this, "Screen rotated!", Toast.LENGTH_SHORT).show();
                c = 2;
            }
            else if(Math.round(Math.abs(z-zo)) == 3 && c!=3) {
                Log.d(TAG, "detectOrientation: ROTATION_180");
                Toast.makeText(this, "Screen rotated!", Toast.LENGTH_SHORT).show();
                c = 3;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        database.fetchRecords();
    }

    @Override
    public void onClick(View v) {
        stop = (Button) v;
        if(stop.getText().equals("Stop")){
            flag = false;
            stop.setText("Start");
            Log.d(TAG, "onClick: Logging stopped");
        }
        else if(stop.getText().equals("Start")){
            flag = true;
            stop.setText("Stop");
            Log.d(TAG, "onClick: Logging started");
        }
    }
}
