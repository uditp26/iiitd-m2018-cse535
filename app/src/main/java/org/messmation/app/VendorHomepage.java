package org.messmation.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.altbeacon.beacon.BeaconManager;
import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.model.CouponRemainingSummary;

public class VendorHomepage extends AppCompatActivity {

    private static final String TAG = homepage.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Intent intent;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private TextView mBreakFastPendingCount;
    private TextView mLunchPendingCount;
    private TextView mEveningTeaPendingCount;
    private TextView mDinnerPendingCount;

    private CouponRemainingSummary couponRemainingSummary;
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.vendor_toolbar);
        mDrawerLayout = findViewById(R.id.vendor_drawer_layout);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.vendor_nav_view);
        final FragmentManager fragmentmanager = getSupportFragmentManager();
        fragment = new CouponHistory();
        fragmentmanager.beginTransaction().add(R.id.vendor_content_frame,fragment)
                .commit();

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
                        switch(id) {
                            case R.id.nav_vendor_coupon:
                                fragment = new CouponHistory();
                                fragmentmanager.beginTransaction().replace(R.id.vendor_content_frame,fragment)
                                        .commit();

                                break;
                            case R.id.nav_vendor_notify:
                                fragment = new NotificationHistory();
                                fragmentmanager.beginTransaction().replace(R.id.vendor_content_frame,fragment)
                                        .commit();
                                break;
                            case R.id.nav_vendor_defaulter:
                                fragment = new DefaulterHistory();
                                fragmentmanager.beginTransaction().replace(R.id.vendor_content_frame,fragment)
                                        .commit();
                                break;
                            case R.id.nav_vendor_markDefaulter:
                                fragment = new MarkDefaulter();
                                fragmentmanager.beginTransaction().replace(R.id.vendor_content_frame,fragment)
                                        .commit();
                                break;
                            case R.id.nav_vendor_signout:
                                signOut();
                                break;
                        }
                        return true;
                    }
                });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ((MessMationApplication)this.getApplicationContext()).initializeAppData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String loggedInUserID = ((MessMationApplication)this.getApplicationContext()).getLoggedinUserUID();
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
}