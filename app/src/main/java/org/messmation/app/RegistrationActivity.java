package org.messmation.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.model.User;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{


    EditText fname, rolln, mail, phone;
    Button submit;
    String uid, email, name, pic;
    Intent intent;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    DBAdapter dbTransaction;
    User user;
    boolean flag;
    public static final String hinttext[] = {"Empty Field!", "Invalid Roll No.", "Use your college mail ID.", "10 digits please!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fname = findViewById(R.id.editText1);
        rolln = findViewById(R.id.editText3);
        mail = findViewById(R.id.editText2);
        //phone = findViewById(R.id.editText4);
        submit = findViewById(R.id.button1);
        //fname.addTextChangedListener(this);
        submit.setOnClickListener(this);
        flag = false;
        // Write a message to the database
        /*fname.setOnFocusChangeListener(this);
        rolln.setOnFocusChangeListener(this);
        //mail.setOnFocusChangeListener(this);
        phone.setOnFocusChangeListener(this);*/

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        try{
            uid = getIntent().getExtras().getStringArray("userD")[0];
            //name = getIntent().getExtras().getStringArray("userD")[1];
            //email = getIntent().getExtras().getStringArray("userD")[2];
            //pic = getIntent().getExtras().getStringArray("userD")[3];
            //((MessMationApplication)this.getApplicationContext()).setLoggedinUserUID(uid);
            //Log.i("UInfo","uid "+uid);
            //Log.i("UInfo","email "+email);
            //Log.i("UInfo","name "+name);
            fname.setText(MessMationApplication.getLoggedinName());
            mail.setText(MessMationApplication.getLoggedinEmail());
            fname.setEnabled(false);
            fname.setFocusable(false);
            mail.setEnabled(false);
            mail.setFocusable(false);
        }catch(NullPointerException npe){
            Log.e("NullPtrEx",npe.toString());
        }
    }

    /*@Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void afterTextChanged(Editable editable) {
        try {
            String s = editable.toString();
            if(!s.matches("[A-Za-z\\s]+")){
                editable.replace(editable.length()-1, editable.length(), "");
                Toast.makeText(this,"Name should only contain letters!",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){}
    }*/

    @Override
    public void onClick(View view) {
        try{
            if(view == submit){
                //flag = validate(fname);
                /*if(flag && rolln.isFocused()){
                    flag = validate(rolln);
                }
                if(flag && mail.isFocused()){
                    flag = validate(mail);
                }*/
                /*if(flag && phone.isFocused()){
                    flag = validate(phone);
                }*/
                //if(validate(fname) && validate(rolln) && validate(mail) && validate(phone)){
                if(validate(mail) && validate(rolln)){
                    flag = true;
                    //update DB
                    dbTransaction = new DBAdapter();
                    user = new User();
                    user.setUuid(MessMationApplication.getLoggedinUserUID());
                    user.setFullName(MessMationApplication.getLoggedinName());
                    user.setEmailId(MessMationApplication.getLoggedinEmail());
                    user.setRollNo(rolln.getText().toString());
                    //user.setPhone(phone.getText().toString());
                    //user.setCity("Delhi");

                    dbTransaction.registerUser(uid,user);

                    Toast.makeText(this, "Registration Complete!", Toast.LENGTH_SHORT).show();
                    intent = new Intent(this, GSignIn.class);
                    //intent.putExtra("name", name);
                    //intent.putExtra("dp", pic);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);   //<-- to clear activity stack
                    startActivity(intent);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        if(!flag){
            Toast.makeText(this,"Registration not complete! Signing out...", Toast.LENGTH_SHORT).show();
            signOut();
        }
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("User","Signed out.");
            }
        });
    }

    public boolean validate(View view) {
        String s;
        if (view == mail) {
            s = mail.getText().toString();
            if (!s.matches(".+@iiitd.ac.in")) {
                mail.setText("");
                mail.setHint(hinttext[2]);
                mail.setHintTextColor(Color.RED);
                Toast.makeText(this, "Not a valid email!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else if (view == rolln) {
            s = rolln.getText().toString();
            if (!(s.matches("20[0-9]{5}") || s.matches("MT[0-9]{5}") || s.matches("PHD[0-9]{5}"))) {
                rolln.setText("");
                rolln.setHint(hinttext[1]);
                rolln.setHintTextColor(Color.RED);
                Toast.makeText(this, "Not a valid roll no.!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        /* else if (view == phone) {
            s = phone.getText().toString();
            if (s.length() < 10) {
                phone.setText("");
                phone.setHint(hinttext[3]);
                phone.setHintTextColor(Color.RED);
                Toast.makeText(this, "Phone No. must be of 10 digits!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (view == fname) {
            s = fname.getText().toString();
            s = s.replaceAll("\\s+", " ");
            if (s.equals("") || s.equals(" ")) {
                fname.setHint(hinttext[0]);
                fname.setHintTextColor(Color.RED);
                Toast.makeText(this, "Name field cannot be empty!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (view == rolln) {
            s = rolln.getText().toString();
            if (!(s.matches("20[0-9]{5}") || s.matches("MT[0-9]{5}") || s.matches("PHD[0-9]{5}"))) {
                rolln.setText("");
                rolln.setHint(hinttext[1]);
                rolln.setHintTextColor(Color.RED);
                Toast.makeText(this, "Not a valid roll no.!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (view == mail) {
            s = mail.getText().toString();
            if (!s.matches(".+@iiitd.ac.in")) {
                mail.setText("");
                mail.setHint(hinttext[2]);
                mail.setHintTextColor(Color.RED);
                Toast.makeText(this, "Not a valid email!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (view == phone) {
            s = phone.getText().toString();
            if (s.length() < 10) {
                phone.setText("");
                phone.setHint(hinttext[3]);
                phone.setHintTextColor(Color.RED);
                Toast.makeText(this, "Phone No. must be of 10 digits!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }*/
        return true;
    }

    /*@Override
    public void onFocusChange(View view, boolean b) {
        String s;
        try{
            if(b){
                if(view == fname){
                    fname.setHint("");
                }
                else if(view == rolln){
                    rolln.setHint("");
                }
                else if(view == mail){
                    mail.setHint("");
                }
                else if(view == phone){
                    phone.setHint("");
                }
            }
            else{
                if(view == fname){
                    s = fname.getText().toString();
                    if(s.equals("")){
                        fname.setHint(hinttext[0]);
                        fname.setHintTextColor(Color.RED);
                        Toast.makeText(this,"Name field cannot be empty!",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(view == rolln){
                    s = rolln.getText().toString();
                    if(!( s.matches("20[0-9]{5}") || s.matches("MT[0-9]{5}") || s.matches("PHD[0-9]{5}"))){
                        rolln.setText("");
                        rolln.setHint(hinttext[1]);
                        rolln.setHintTextColor(Color.RED);
                        Toast.makeText(this,"Not a valid roll no.!",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(view == mail){
                    s = mail.getText().toString();
                    if(!s.matches(".+@.+")){
                        mail.setText("");
                        mail.setHint(hinttext[2]);
                        mail.setHintTextColor(Color.RED);
                        Toast.makeText(this,"Not a valid email!",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(view == phone){
                    s = phone.getText().toString();
                    if(s.length() < 10){
                        phone.setText("");
                        phone.setHint(hinttext[3]);
                        phone.setHintTextColor(Color.RED);
                        Toast.makeText(this,"Phone No. must be of 10 digits!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch(Exception e){}
    }*/
}
