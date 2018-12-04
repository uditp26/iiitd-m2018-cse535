package org.messmation.app.beacon;

import android.app.Application;

import android.net.Uri;
import android.util.Log;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.messmation.app.CouponTasks;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.homepage;
import org.messmation.app.model.Config;
import org.messmation.app.model.CouponCreditDetail;
import org.messmation.app.model.CouponDebitHistory;
import org.messmation.app.model.CouponNotifHistory;
import org.messmation.app.model.CouponRemainingSummary;
import org.messmation.app.model.User;

public class MessMationApplication extends Application implements BootstrapNotifier {

    protected static final String TAG = MessMationApplication.class.getSimpleName();

    private static String loggedinUserUID;
    private static String loggedinName;
    private static String loggedinEmail;
    private static Uri loggedinPhoto;
    private static String loggedinPhone;
    private static Boolean paymentStatus;
    private static CouponRemainingSummary couponRemainingSummary;
    private static User newUser;
    private static Config applicationConfig;
    public static CouponDebitHistory currentDayCouponDebits;
    private static CouponNotifHistory currentDayCouponNotifs;


    private static CouponCreditDetail currentMonthCouponCreditDetail = null;



    private BackgroundPowerSaver backgroundPowerSaver;
    //private homepage mainActivity = null;
    private RegionBootstrap regionBootstrap;

    public static String getLoggedinName() {
        return loggedinName;
    }

    public static void setLoggedinName(String loggedinName) {
        MessMationApplication.loggedinName = loggedinName;
    }

    public static String getLoggedinEmail() {
        return loggedinEmail;
    }

    public static void setLoggedinEmail(String loggedinEmail) {
        MessMationApplication.loggedinEmail = loggedinEmail;
    }

    public static Uri getLoggedinPhoto() {
        return loggedinPhoto;
    }

    public static void setLoggedinPhoto(Uri loggedinPhoto) {
        MessMationApplication.loggedinPhoto = loggedinPhoto;
    }

    public static Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public static void setPaymentStatus(Boolean paymentStatus) {
        MessMationApplication.paymentStatus = paymentStatus;
    }

    public static String getLoggedinPhone() {
        return loggedinPhone;
    }

    public static void setLoggedinPhone(String loggedinPhone) {
        MessMationApplication.loggedinPhone = loggedinPhone;
    }
    public static User getNewUser() {
        return newUser;
    }

    public static void setNewUser(User newUser) {
        MessMationApplication.newUser = newUser;
    }

    public static Config getApplicationConfig() {
        Log.d(TAG, "Inside getApplicationConfig " + applicationConfig);
        return applicationConfig;
    }

    public static CouponDebitHistory getCurrentDayCouponDebits() {
        Log.d(TAG, "Inside getCurrentDayCouponDebits " + currentDayCouponDebits);
        return currentDayCouponDebits;
    }


    public static void setCouponRemainingSummary(CouponRemainingSummary couponRemainingSummary) {
        MessMationApplication.couponRemainingSummary = couponRemainingSummary;
    }

    public static CouponRemainingSummary getCouponRemainingSummary() {
        Log.d(TAG, "Inside getCouponRemainingSummary " + couponRemainingSummary);
        return couponRemainingSummary;
    }

    public static String getLoggedinUserUID() {
        return loggedinUserUID;
    }

    public void setLoggedinUserUID(String loggedinUserUID) {
        this.loggedinUserUID = loggedinUserUID;
    }

    public static CouponNotifHistory getCurrentDayCouponNotifs() {
        return currentDayCouponNotifs;
    }


    public static CouponCreditDetail getCouponCreditForMonth() {
        return currentMonthCouponCreditDetail;
    }

    public void onCreate() {
        super.onCreate();

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1500l);

        Log.d(TAG, "setting up background monitoring for beacons and power saving");

        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);

        new DBAdapter().getAppConfiguration(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                applicationConfig = (Config) obj;
                Log.d(TAG, "Data Retrieved MessMationApplication getAppConfiguration " + applicationConfig.getBreakFastTimings());
            }
        });

    }

    @Override
    public void didEnterRegion(Region region) {

        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        //regionBootstrap.disable();
        Log.d(TAG, "Inside didEnterRegion method");
//        if (mainActivity != null) {
//            // If the Monitoring Activity is visible, we log info about the beacons we have
//            // seen on its display
//            //mainActivity.logToDisplay("I see a beacon again");
//            Log.d(TAG, "Inside didEnterRegion, I see a beacon again");
//        } else {
            // If we have already seen beacons before, but the monitoring activity is not in
            // the foreground, we send a notification to the user on subsequent detections.
            Log.d(TAG, "Inside didEnterRegion, Beacon Seen.");
            CouponTasks.processBeaconInSight(getApplicationContext());
       // }
        //}
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "did exit region.");
//        if (mainActivity != null) {
//            //monitoringActivity.logToDisplay("I no longer see a beacon.");
//            Log.d(TAG, "I no longer see a beacon");
//        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(TAG, "Inside didDetermineStateForRegion region");
//        if (mainActivity != null) {
//            //monitoringActivity.logToDisplay("I have just switched from seeing/not seeing beacons: " + state);
//            Log.d(TAG, "I have just switched from seeing/not seeing beacons: ");
//        }
    }

    public static void initializeAppData() {
        new DBAdapter().getCurrentDayCouponDebitDetails(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                currentDayCouponDebits = (CouponDebitHistory) obj;
                Log.d(TAG, "Data Retrieved MessMationApplication " + currentDayCouponDebits);
            }
        }, loggedinUserUID);

        new DBAdapter().getRemainingCouponsDetails(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                couponRemainingSummary = (CouponRemainingSummary) obj;
                Log.d(TAG, "Data Retrieved MessMationApplication " + couponRemainingSummary);
            }
        }, loggedinUserUID);

        new DBAdapter().getCurrentDayCouponNotifDetails(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                currentDayCouponNotifs = (CouponNotifHistory) obj;
                Log.d(TAG, "Data Retrieved MessMationApplication getCurrentDayCouponNotifDetails " + currentDayCouponNotifs);
            }
        }, loggedinUserUID);

        new DBAdapter().getCurrentMonthCouponCreditDetails(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                currentMonthCouponCreditDetail = (CouponCreditDetail) obj;
                Log.d(TAG, "Data Retrieved MessMationApplication getCurrentMonthCouponCreditDetails " + currentMonthCouponCreditDetail);
            }
        }, loggedinUserUID);
    }
}