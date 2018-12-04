package org.messmation.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.model.Config;
import org.messmation.app.model.User;
import org.messmation.app.model.Vendor;

import java.net.URI;

public class GSignIn extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = GSignIn.class.getSimpleName();
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    private static int RC_SIGN_IN = 1;
    //private static int V_REG = 2;
    Intent intent;
    String udetails[] = new String[3];
    Vendor vendor;
    User user = null;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsign_in);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleSignInClient.silentSignIn();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account==null){
            // Update UI to display the Google Sign-in button.
            Log.i("SignIn","New User!");
            signInButton.setVisibility(View.VISIBLE);
        }
        else{
            //hide the sign-in button; launch main activity
            //done
            Log.i("SignIn","User is already signed in!");
            signInButton.setVisibility(View.GONE);
            udetails[0] = account.getId();
            udetails[1] = account.getDisplayName();
            udetails[2] = account.getEmail();
            uri = account.getPhotoUrl();
            //Log.i(TAG, "Photo" + uri.getPath());
            ((MessMationApplication)this.getApplicationContext()).setLoggedinUserUID(udetails[0]);
            ((MessMationApplication)this.getApplicationContext()).setLoggedinName(udetails[1]);
            ((MessMationApplication)this.getApplicationContext()).setLoggedinEmail(udetails[2]);
            if(uri != null){
                ((MessMationApplication)this.getApplicationContext()).setLoggedinPhoto(uri);
            }
            else{
                ((MessMationApplication)this.getApplicationContext()).setLoggedinPhoto(null);
            }


            new DBAdapter().getUser(new FirebaseCallback() {

                @Override
                public void onCompletionOfFirebaseCall(Object obj) {
                    user = (User)obj;
                    Log.d(TAG, "Data Retrieved user " + user);

                    if(user!=null){     // user signed in and registered
                        MessMationApplication.setNewUser(user);
                        intent = new Intent(getApplicationContext(), homepage.class);
                        //intent.putExtra("name", udetails[1]);
                        //intent.putExtra("dp", udetails[3]);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);   //<-- to clear activity stack
                        startActivity(intent);
                        finish();
                    }else {
                        new DBAdapter().getVendor(new FirebaseCallback() {
                            @Override
                            public void onCompletionOfFirebaseCall(Object obj) {
                                vendor = (Vendor) obj;
                                if (vendor != null) {
                                    Toast.makeText(GSignIn.this, "Vendor Log In", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(GSignIn.this, VendorHomepage.class);
                                    intent.putExtra("name","tushar");
                                    startActivity(intent);
                                } else {
                                    // user signed in but not registered
                                    startRegistration();
                                }
                            }
                        }, account.getId());
                    }
                }
            },account.getId());

        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sign_in_button){
            performSignIn();
        }
    }

    public void performSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            handleSignInResult(task);
        }
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            account = completedTask.getResult(ApiException.class);
            updateUI();
        }catch(ApiException e){
            //e.printStackTrace();
            Log.w("APIEXCEPTION", "signInResult:failed code = " + e.getStatusCode());
            updateUI();
        }
    }

    public void updateUI(){
        if(account==null){
            Log.d("Test1","didn't start reg!");
            Toast.makeText(this, "Registration aborted!", Toast.LENGTH_SHORT).show();
            //check for network and display appropriate errors
        }
        else{
            Log.d("Test","start reg!");
            udetails[0] = account.getId();
            udetails[1] = account.getDisplayName();
            udetails[2] = account.getEmail();
            uri = account.getPhotoUrl();
            Log.i(TAG, "Name " + udetails[1]);
            //Log.i(TAG, "Photo " + uri.getPath());
            ((MessMationApplication)this.getApplicationContext()).setLoggedinUserUID(udetails[0]);
            ((MessMationApplication)this.getApplicationContext()).setLoggedinName(udetails[1]);
            ((MessMationApplication)this.getApplicationContext()).setLoggedinEmail(udetails[2]);
            if(uri != null){
                ((MessMationApplication)this.getApplicationContext()).setLoggedinPhoto(uri);
            }
            else{
                ((MessMationApplication)this.getApplicationContext()).setLoggedinPhoto(null);
            }
            new DBAdapter().getUser(new FirebaseCallback() {

                @Override
                public void onCompletionOfFirebaseCall(Object obj) {
                    user = (User)obj;
                    Log.d(TAG, "Data Retrieved user " + user);
                    //Log.d(TAG,"Name: " + udetails[1]);
                    if(user!=null){
                        MessMationApplication.setNewUser(user);
                        intent = new Intent(getApplicationContext(), homepage.class);
                        //intent.putExtra("name", udetails[1]);
                        //intent.putExtra("dp", udetails[3]);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);   //<-- to clear activity stack
                        startActivity(intent);
                        finish();
                    }else{
                        Log.d("TAG", "Vendor Adapter");
                        new DBAdapter().getVendor(new FirebaseCallback() {
                            @Override
                            public void onCompletionOfFirebaseCall(Object obj) {
                                vendor = (Vendor) obj;
                                if (vendor != null) {
                                    Toast.makeText(GSignIn.this, "Vendor Log In", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(GSignIn.this, VendorHomepage.class);
                                    intent.putExtra("name","tushar");
                                    startActivity(intent);
                                } else {
                                    startRegistration();
                                }
                            }
                        }, account.getId());
                    }
                }
            },account.getId());
        }
    }

    private void startRegistration(){
        intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        intent.putExtra("userD", udetails);
        startActivity(intent);
    }
}

