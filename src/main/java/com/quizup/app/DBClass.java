package com.quizup.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DBClass extends SQLiteOpenHelper {

    private static final  String DB_NAME = "quizinfo";
    private static final int DB_VERSION = 1;
    SQLiteDatabase db;
    Context context;
    Cursor cursor;

    DBClass(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    private void insertQuestion(SQLiteDatabase db, String ques, Boolean ans){
        ContentValues qvalue = new ContentValues();
        qvalue.put("Question",ques);
        qvalue.put("Answer",ans);
        db.insert("DETAILS",null,qvalue);
    }

    /*public SQLiteDatabase getDb() {
        return db;
    }*/

    public void setReference(SQLiteDatabase db){
        this.db = db;
    }

    private void createUserTable(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("CREATE TABLE ANSWER(Qno INTEGER, " + "UAnswer TEXT);");
        Log.i("UpdateDB","ANSWER Table creation successful!");
        //db.execSQL("CREATE TABLE ANSWER(Sno INTEGER, " + "UAnswer NUMERIC, " + "FOREIGN KEY(Sno) REFERENCES DETAILS(Sno));");
    }

    public void insertRecord(int pos, String value){
        ContentValues cv = new ContentValues();
        cv.put("Qno", pos);
        cv.put("UAnswer", value);
        cursor = db.query("ANSWER", new String[]{"Qno"}, "Qno=?", new String[]{Integer.toString(pos)}, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            db.insert("ANSWER", null, cv);
            Log.i("UpdateDB","User answer recorded!");
        }
        else{
            db.update("ANSWER", cv,"Qno=?", new String[]{Integer.toString(pos)});
            Log.i("UpdateDB","User answer overwritten!");
        }
        cursor.close();
    }

    public String checkStatus(int pos){
        cursor = db.query("ANSWER", new String[]{"Qno", "UAnswer"}, "Qno=?", new String[]{Integer.toString(pos)}, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            return null;
        }
        else{
            return cursor.getString(1);
        }
    }

    private void updateDB(SQLiteDatabase db, int oldver, int newver){
        db.execSQL("CREATE TABLE DETAILS(Sno INTEGER PRIMARY KEY AUTOINCREMENT, " + "Question TEXT, " + "Answer NUMERIC);");
        insertQuestion(db, "The Language that the computer can understand is called Machine Language.",Boolean.TRUE);
        insertQuestion(db,"Magnetic Tape used random access method.",Boolean.FALSE);
        insertQuestion(db,"Twitter is an online social networking and blogging service.", Boolean.TRUE);
        insertQuestion(db, "Worms and trojan horses are easily detected and eliminated by antivirus software.", Boolean.TRUE);
        insertQuestion(db,"Dot-matrix, Deskjet, Inkjet and Laser are all types of Printers.",Boolean.TRUE);
        insertQuestion(db,"GNU / Linux is a open source operating system.", Boolean.TRUE);
        insertQuestion(db,"Whaling / Whaling attack is a kind of phishing attacks that target senior executives and other high profile to access valuable information",Boolean.TRUE);
        insertQuestion(db,"Freeware is software that is available for use at no monetary cost.",Boolean.TRUE);
        insertQuestion(db,"IPv6 Internet Protocol address is represented as eight groups of four Octal digits.", Boolean.FALSE);
        insertQuestion(db,"The hexadecimal number system contains digits from 1 - 15.",Boolean.FALSE);
        insertQuestion(db,"Octal number system contains digits from 0 - 7.",Boolean.TRUE);
        insertQuestion(db,"MS Word is a hardware.", Boolean.FALSE);
        insertQuestion(db,"CPU controls only input data of computer.",Boolean.FALSE);
        insertQuestion(db,"CPU stands for Central Performance Unit.", Boolean.FALSE);
        insertQuestion(db,"When you include multiple addresses in a message, you should separate each address with a period (.)",Boolean.FALSE);
        insertQuestion(db,"You cannot format text in an e-mail message.",Boolean.FALSE);
        insertQuestion(db,"You must include a subject in any mail message you compose.", Boolean.FALSE);
        insertQuestion(db,"You can store Web-based e-mail messages in online folders.",Boolean.TRUE);
        insertQuestion(db,"You can delete e-mails from a Web-based e-mail account.", Boolean.TRUE);
        insertQuestion(db,"Web-based e-mail accounts do not required passwords.", Boolean.FALSE);
        insertQuestion(db,"You can sign up for Web-based e-mail without accepting the Web site's terms and conditions.",Boolean.FALSE);
        insertQuestion(db,"Your e-mail address must be unique.", Boolean.TRUE);
        insertQuestion(db,"You cannot send a file from a Web-based e-mail account.", Boolean.FALSE);
        insertQuestion(db,"There is only one way to create a new folder.", Boolean.FALSE);
        insertQuestion(db,"New folders must all be at the same level.", Boolean.FALSE);
        insertQuestion(db,"You can only store messages in a new folder if they are received after you create the folder.",Boolean.FALSE);
        insertQuestion(db,"In Outlook, you must store all of your messages in the Inbox.",Boolean.FALSE);
        insertQuestion(db,"You can only send one attachment per e-mail message.", Boolean.FALSE);
        insertQuestion(db,"It is impossible to send a worm or virus over the Internet using in attachment.",Boolean.FALSE);
        insertQuestion(db,"All attachments are safe.",Boolean.FALSE);
        Log.i("UpdateDB","Insertion successful!");
    }

    public void printFile(File f){
        String s="";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String l = br.readLine();
            while(l!=null){
                sb.append(l);
                sb.append(System.lineSeparator());
                l = br.readLine();
            }
            s += sb.toString();
        } catch (Exception e) {
            Log.e("Exception","File Not Found");
        }
        Log.i("FileContent ", s);
    }

    public File exporttoCSV(){
        Boolean flag = false;
        File dir = new File(context.getFilesDir(), "");     // Environment.getExternalStorageDirectory() - request for permissions dynamically
        //Log.i("FilePath", dir.toString());    ////<---- start here: upload to server(Async Task)
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, "answers.csv");
        Log.i("FilePath", file.toString());
        try{
            PrintWriter printWriter = new PrintWriter(file);
            cursor = db.query("ANSWER", new String[]{"Qno", "UAnswer"}, null, null, null, null, null);
            cursor.moveToFirst();
            if(cursor.getCount()==0){
                Toast.makeText(context,"No records to export!", Toast.LENGTH_SHORT).show();
            }
            else{
                flag = true;
                do{
                    StringBuilder stringBuilder = new StringBuilder();
                    int qno = cursor.getInt(0);
                    String value = cursor.getString(1);
                    stringBuilder.append(qno);
                    stringBuilder.append(',');
                    stringBuilder.append(value);
                    stringBuilder.append('\n');
                    printWriter.write(stringBuilder.toString());
                }
                while(cursor.moveToNext());
                printWriter.close();
                cursor.close();
                Log.i("ExportFile", "File exported successfully!");
                Toast.makeText(context, "File export complete.", Toast.LENGTH_SHORT).show();
                printFile(file);
            }
        }
        catch(Exception e){
            Log.e("Exception", e.getMessage());
        }
        if(flag)
            return file;
        else
            return null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;
        updateDB(sqLiteDatabase, 0, DB_VERSION);
        createUserTable(sqLiteDatabase);
        db.setVersion(1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        this.db = sqLiteDatabase;
        updateDB(sqLiteDatabase, i, i1);
    }
}

