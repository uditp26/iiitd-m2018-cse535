package org.messmation.app;

import android.content.Context;
import android.util.Log;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.model.Config;
import org.messmation.app.model.CouponDebitDetail;
import org.messmation.app.model.CouponDebitHistory;
import org.messmation.app.utilities.NotificationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CouponTasks {

    private static final String TAG = CouponTasks.class.getSimpleName();


    public static final String ACTION_DEDUCT_COUPON = "deduct-coupon";
    public static final String ACTION_IGNORE_NOTIFICATION = "dismiss-notification";
    static final String ACTION_BEACON_SEEN = "beacon-seen";

    public static final String BREAKFAST = "Breakfast";
    public static final String LUNCH = "Lunch";
    public static final String EVENINGTEA = "Evening Tea";
    public static final String DINNER = "Dinner";

    public static int executeTask(Context context, String action) {
        int status = -1;
        if (ACTION_DEDUCT_COUPON.equals(action)) {
            status = deductCoupon(context);
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_IGNORE_NOTIFICATION.equals(action)) {
            Log.d(TAG,"Inside ignoring coupon deduction notification");
            logCouponNotificationAction(context,"Ignored");
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_BEACON_SEEN.equals(action)) {
            processBeaconInSight(context);
        }

        return status;
    }

    private static int deductCoupon(Context context) {
        int status = -1;
        Log.d(TAG,"Inside processing coupon deduction notification");
        DBAdapter dbAdapter = new DBAdapter();

        String loggedInUserID = MessMationApplication.getLoggedinUserUID();

        String mealName = determinCouponNameFromTime();
        if(mealName!=null) {
            status = dbAdapter.debitCoupon(loggedInUserID, mealName);
            if(status==0){ //on successful coupon deduction, update notification status
                logCouponNotificationAction(context,"Deducted");
            }
        }else{
            Log.w(TAG,"Error processing config data, cannot proceed deducting coupons");
        }
        return status;
    }

    private static void logCouponNotificationAction(Context context, String notifAction) {
        Log.d(TAG,"Inside processing coupon deduction notification");
        DBAdapter dbAdapter = new DBAdapter();

        String loggedInUserID = MessMationApplication.getLoggedinUserUID();

        String mealName = determinCouponNameFromTime();
        if(mealName!=null) {
            dbAdapter.logCouponNotificationAction(loggedInUserID, mealName,notifAction);

        }else{
            Log.w(TAG,"Error processing config data, cannot proceed deducting coupons");
        }
    }

    public static void processBeaconInSight(Context context) {
        Log.d(TAG,"Inside processBeaconInSight");

        String mealName = determinCouponNameFromTime();

        Log.d(TAG,"Inside processBeaconInSight mealName "+mealName);

        if(mealName!=null) {
            boolean couponAlreadyDeducted = checkIfCouponAlreadyDeductedForCurrentMeal(mealName);
            if (!couponAlreadyDeducted) {
                NotificationUtils.showCouponReminderNotification(context); //show coupon deduction notification
                logCouponNotificationAction(context,"Created"); //notification created
            }else{
                Log.d(TAG,"Already deducted coupon for this meal");
            }

        }else{
            Log.d(TAG, "Coupon notification not generated as it is not time for any meal yet");
        }
    }

    public static boolean checkIfCouponAlreadyDeductedForCurrentMeal(String mealName){
        Log.d(TAG, "Inside checkIfCouponAlreadyDeductedForCurrentMeal with mealName" +mealName);
        Log.d(TAG, "Inside checkIfCouponAlreadyDeductedForCurrentMeal MessMationApplication.getCurrentDayCouponDebits() " + MessMationApplication.getCurrentDayCouponDebits());

        CouponDebitHistory currentDayCouponDebits = MessMationApplication.getCurrentDayCouponDebits();

        List<CouponDebitDetail> couponDebitDetailList;

        boolean couponAlreadyDeducted = false;

        if(currentDayCouponDebits==null){
            couponDebitDetailList = new ArrayList<CouponDebitDetail>();
        }else {
            couponDebitDetailList = currentDayCouponDebits.getCouponDebitList();

            Object[] couponDebitDetailObjArr = couponDebitDetailList.toArray();

            for (int i = 0; i < couponDebitDetailObjArr.length; i++) {

                HashMap<String, String> couponDebitDetailObj = (HashMap) couponDebitDetailObjArr[i];

                Log.d(TAG, "Inside couponDebitDetailList " + couponDebitDetailObj);

                if (mealName.equalsIgnoreCase(couponDebitDetailObj.get("mealName"))) {
                    couponAlreadyDeducted = true;
                }
            }
        }

        Log.d(TAG,"Inside checkIfCouponAlreadyDeductedForCurrentMeal with couponAlreadyDeducted "+couponAlreadyDeducted);

        return couponAlreadyDeducted;
    }

    //TODO check what happens if mobile date/time is changed
    public static String determinCouponNameFromTime(){
        Config config = MessMationApplication.getApplicationConfig();
        if(config!=null) {
            Log.d(TAG, "Inside determinCouponNameFromTime, config present " + config.getBreakFastTimings());
            boolean isBreakFast = checkIfDateAllowedForBooking(convertTimeToCurrentDateTime(config.getBreakFastTimings(),0), convertTimeToCurrentDateTime(config.getBreakFastTimings(),1));
            if(isBreakFast){
                return BREAKFAST;
            }

            boolean isLunch = checkIfDateAllowedForBooking(convertTimeToCurrentDateTime(config.getLunchTimings(),0), convertTimeToCurrentDateTime(config.getLunchTimings(),1));
            if(isLunch){
                return LUNCH;
            }

            boolean isEveningTea = checkIfDateAllowedForBooking(convertTimeToCurrentDateTime(config.getEveningTeaTimings(),0), convertTimeToCurrentDateTime(config.getEveningTeaTimings(),1));
            if(isEveningTea){
                return  EVENINGTEA;
            }

            boolean isDinner = checkIfDateAllowedForBooking(convertTimeToCurrentDateTime(config.getDinnerTimings(),0), convertTimeToCurrentDateTime(config.getDinnerTimings(),1));
            if(isDinner){
                return DINNER;
            }
        }
        return null;
    }

    /**
     * This method returns current date object by appending input timings
     * @param timeRange
     * @param partNo - partNo 0 indicates meal start time, partNo 1 indicates meal end time
     * @return
     */
    private static Date convertTimeToCurrentDateTime(String timeRange, int partNo){

        String timeString = null;
        if(partNo==0){ //1st part
            timeString = timeRange.substring(0,timeRange.indexOf("-"));
        }else if(partNo == 1){
            timeString = timeRange.substring(timeRange.indexOf("-")+1, timeRange.length());
        }

        Log.d(TAG,"timeString "+timeString);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if(timeString!=null){
            String hh = timeString.split(":")[0];
            String mm = timeString.split(":")[1];
            cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hh));
            cal.add(Calendar.MINUTE, Integer.parseInt(mm));
        }

        return cal.getTime();
    }

    private static boolean checkIfDateAllowedForBooking(Date startDate, Date endDate) {
        Log.d(TAG,"Inside checkIfDateAllowedForBooking");
        Date ipDate = new Date();
        boolean isInRange = !(ipDate.before(startDate) || ipDate.after(endDate));
        Log.d(TAG,"Inside checkIfDateAllowedForBooking startDate "+startDate + " endDate "+endDate + " ipDate "+ipDate);
        Log.d(TAG,"Inside checkIfDateAllowedForBooking isInRange "+isInRange);
        return isInRange;
    }
}