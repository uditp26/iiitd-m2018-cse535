package com.appsense.app.appsense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.util.Log;

public class SensorDB extends SQLiteOpenHelper {

    private static final String TAG = "SensorDB";

    private static final  String DB_NAME = "sensordb";
    private static final int DB_VERSION = 1;
    SQLiteDatabase db;
    Context context;
    Cursor cursor;
    ContentValues cv;

    SensorDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        createTables();
        this.db.setVersion(db.getVersion() + 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        db.setVersion(newVersion);
    }

    public void initDB(SQLiteDatabase db){
        this.db = db;
    }

    private void createTables(){
        db.execSQL("CREATE TABLE IF NOT EXISTS ACCLOGS(Xcord FLOAT, " + "Ycord FLOAT, " + "Zcord FLOAT, " + "TimeStamp TEXT);");
        Log.i(TAG, "createTable: ACCLOGS Table creation successful.");
        db.execSQL("CREATE TABLE IF NOT EXISTS GYROLOGS(Xcord FLOAT, " + "Ycord FLOAT, " + "Zcord FLOAT, " + "TimeStamp TEXT);");
        Log.i(TAG, "createTable: GYROLOGS Table creation successful.");
        db.execSQL("CREATE TABLE IF NOT EXISTS PROXLOGS(Xcord FLOAT, " + "TimeStamp TEXT);");
        Log.i(TAG, "createTable: PROXLOGS Table creation successful.");
        db.execSQL("CREATE TABLE IF NOT EXISTS ORTNLOGS(Xcord FLOAT, " + "Ycord FLOAT, " + "Zcord FLOAT, " + "TimeStamp TEXT);");
        Log.i(TAG, "createTable: ORTNLOGS Table creation successful.");
        db.execSQL("CREATE TABLE IF NOT EXISTS GPSLOGS(Xcord FLOAT, " + "Ycord FLOAT, " + "Zcord FLOAT, " + "TimeStamp TEXT);");
        Log.i(TAG, "createTable: GPSLOGS Table creation successful.");
    }

    public void insertRecord(int sensorType, double x, double y, double z, String timestamp){
        cv = new ContentValues();
        cv.put("Xcord", x);
        cv.put("TimeStamp", timestamp);
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            cv.put("Ycord", y);
            cv.put("Zcord", z);
            db.insert("ACCLOGS", null, cv);
            Log.i(TAG, "insertRecord: Readings inserted into ACCLOGS");
        }
        else if(sensorType == Sensor.TYPE_GYROSCOPE){
            cv.put("Ycord", y);
            cv.put("Zcord", z);
            db.insert("GYROLOGS", null, cv);
            Log.i(TAG, "insertRecord: Readings inserted into GYROLOGS");
        }
        else if(sensorType == Sensor.TYPE_PROXIMITY){
            db.insert("PROXLOGS", null, cv);
            Log.i(TAG, "insertRecord: Readings inserted into PROXLOGS");
        }
        else if(sensorType == Sensor.TYPE_ORIENTATION){
            cv.put("Ycord", y);
            cv.put("Zcord", z);
            db.insert("ORTNLOGS", null, cv);
            Log.i(TAG, "insertRecord: Readings inserted into ORTNLOGS");
        }
        else{
            cv.put("Ycord", y);
            cv.put("Zcord", z);
            db.insert("GPSLOGS", null, cv);
            Log.i(TAG, "insertRecord: Readings inserted into GPSLOGS");
        }
    }

    public void fetchRecords(){
        cursor = db.query("ACCLOGS", new String[]{"Xcord", "Ycord", "Zcord", "TimeStamp"}, null, null, null, null, null);
        cursor.moveToFirst();
        Log.d(TAG, "fetchRecords: ACCLOGS " + cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
        while(cursor.moveToNext()){
            Log.d(TAG, "fetchRecords: ACCLOGS " + cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
        }
        cursor.close();
    }
}
