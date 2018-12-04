package org.messmation.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.altbeacon.beacon.BeaconManager;
import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.model.CouponCreditDetail;
import org.messmation.app.model.CouponRemainingSummary;
import org.messmation.app.model.User;
import org.messmation.app.model.UserCouponHistory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class homepage extends AppCompatActivity{

    private static final String TAG = homepage.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Intent intent;
    Bitmap bitmap;
    Uri pic;
    URL url;
    String name, email;
    ImageView imageView;
    TextView nameView, mailView;
    View navHeaderView;
    FrameLayout frameLayout;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private CouponRemainingSummary couponRemainingSummary;
    private UserCouponHistory uch;
    private User user;
    private List<CouponCreditDetail> couponCreditList;
    CouponCreditDetail ccd;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        frameLayout = (FrameLayout)toolbar.getParent();
        frameLayout.bringChildToFront(toolbar);

        final FragmentManager fragmentmanager = getSupportFragmentManager();

        Log.d(TAG, " toolbar "+toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        Log.d(TAG, " actionbar "+actionbar);

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Intent intent=getIntent();

        //pic = getIntent().getStringExtra("dp");
        //name = getIntent().getStringExtra("name");
        pic = MessMationApplication.getLoggedinPhoto();
        name = MessMationApplication.getLoggedinName();
        email = MessMationApplication.getLoggedinEmail();
        Log.i(TAG,"Name: " + name);
        Log.i(TAG,"Photo: " + pic);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navHeaderView = navigationView.getHeaderView(0);
        navHeaderView.setBackgroundResource(R.drawable.nav_head);
        imageView = navHeaderView.findViewById(R.id.imgProfile);
        //if(pic != null){
            //ImgDownloader obj = new ImgDownloader(pic, imageView);
            //obj.execute();
        //}
        //else{
            imageView.setImageResource(R.drawable.profpic_empty);
        //}
        nameView = navHeaderView.findViewById(R.id.username);
        nameView.setText(name);
        mailView = navHeaderView.findViewById(R.id.mail);
        mailView.setText(email);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ((MessMationApplication)this.getApplicationContext()).initializeAppData();

        verifyLocationPermission();

        verifyBluetooth();

        //check payment status from database and disable fields accordingly

        fragment = new FragHomepage();
        fragmentmanager.beginTransaction().add(R.id.content_frame, fragment).commit();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_coupon) {
                            Log.d(TAG, "Nav " + id);
                            fragment = new FragHomepage();
                            fragmentmanager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        } else if (id == R.id.nav_deduct_coupon) {
                            Log.d(TAG, "Deduct " + id);
                            fragment = new ManualCoupon();
                            fragmentmanager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            //Intent intent2 =new Intent(getApplicationContext(), ManualCoupon.class);
                            //startActivity(intent2);

                        } else if (id == R.id.nav_buy_coupon) {
                            Log.d(TAG, "Buy " + id);
                            fragment = new buyCoupon();
                            fragmentmanager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            //Intent intent3 = new Intent(getApplicationContext(), buyCoupon.class);
                            //startActivity(intent3);

                        } else if (id == R.id.nav_signout) {
                            signOut();
                        }
                        return true;
                    }
                });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

          ((MessMationApplication)this.getApplicationContext()).initializeAppData();

          verifyLocationPermission();

            verifyBluetooth();

        //check payment status from database and disable fields accordingly

    }


    @Override
    protected void onStart() {
        super.onStart();
        /*String loggedInUserID = ((MessMationApplication)this.getApplicationContext()).getLoggedinUserUID();

        Log.d(TAG, "Inside onStart method of homePage with "+loggedInUserID);

        new DBAdapter().getRemainingCouponsDetails(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                couponRemainingSummary = (CouponRemainingSummary)obj;
                Log.d(TAG, "Data Retrieved getRemianingCouponsDetails " + couponRemainingSummary);
                if(couponRemainingSummary!=null) {
                    MessMationApplication.setCouponRemainingSummary(couponRemainingSummary);
                    mBreakFastPendingCount.setText(String.valueOf(couponRemainingSummary.getBreakFastRemainingCoupons()));
                    mLunchPendingCount.setText(String.valueOf(couponRemainingSummary.getLunchRemainingCoupons()));
                    mEveningTeaPendingCount.setText(String.valueOf(couponRemainingSummary.getEveningTeaRemainingCoupons()));
                    mDinnerPendingCount.setText(String.valueOf(couponRemainingSummary.getDinnerRemainingCoupons()));
                }
            }
        },loggedInUserID);*/
    }

    private void verifyLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("User","Signed out.");
                intent = new Intent(getApplicationContext(), GSignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.finish();
    }

}

/*class ImgDownloader extends AsyncTask<Void, Void, Void>{
    URL url;
    Bitmap bitmap;
    Uri pic;
    ImageView imageView;

    ImgDownloader(){}

    ImgDownloader(Uri pic, ImageView imageView){
        this.pic = pic;
        this.imageView = imageView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            url = new URL(pic.toString());
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("ImgDownloader", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}*/