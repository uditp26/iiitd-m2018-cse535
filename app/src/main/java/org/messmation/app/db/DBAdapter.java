package org.messmation.app.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.messmation.app.CouponTasks;
import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.model.Config;
import org.messmation.app.model.CouponCreditDetail;
import org.messmation.app.model.CouponDebitDetail;
import org.messmation.app.model.CouponDebitHistory;
import org.messmation.app.model.CouponNotifDetail;
import org.messmation.app.model.CouponNotifHistory;
import org.messmation.app.model.CouponRemainingSummary;
import org.messmation.app.model.User;
import org.messmation.app.model.Vendor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DBAdapter {

    private static final String TAG = DBAdapter.class.getSimpleName();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private User user;
    private static int [] mealCount = new int[4];
    private CouponDebitDetail couponDebitDetail;
    int counter, priority, markPriority;
    String emailPriority="", fullName, namePriority;
    ArrayList<String> emailPriorityDataSet = new ArrayList<>();
    public void registerUser(String uid, User user) {
        mUserDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid);
        mUserDatabaseReference.setValue(user);
    }

    public void creditCoupons(String uid, int coupons, Date forMonth, Date validityUpto, String transactionID, double couponSetAmount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        String couponCreditMonth = sdf.format(forMonth);

        mUserDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid);

        if (mUserDatabaseReference != null) { //TODO check if uid not found


            Log.d(TAG, "Inside creditCoupons mUserDatabaseReference " + mUserDatabaseReference + "couponCreditMonth " + couponCreditMonth);

            CouponCreditDetail couponCreditDetail = new CouponCreditDetail(couponCreditMonth, sdfWithTime.format(validityUpto), coupons, transactionID, couponSetAmount, sdfWithTime.format(new Date()));

            mUserDatabaseReference.child("userCouponHistory").child(couponCreditMonth).setValue(couponCreditDetail);

        }

    }


    public int debitCoupon(String uid, String mealName) {

        Log.d(TAG,"Inside debitCoupon with uid "+uid + " mealName "+mealName);

        int status = -1;

        SimpleDateFormat sdfForMonth = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String currentDateStr = sdf.format(new Date());
        mUserDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid);

        String forMonth = sdfForMonth.format(new Date());

        if (mUserDatabaseReference != null) { //TODO check if uid not found
            Log.d(TAG, "Inside debitCoupon mUserDatabaseReference " + mUserDatabaseReference);
            CouponDebitDetail couponDebitDetail = new CouponDebitDetail(mealName, currentDateStr, sdfWithTime.format(new Date()));

            boolean couponAlreadyDeducted = CouponTasks.checkIfCouponAlreadyDeductedForCurrentMeal(mealName);

            if (!couponAlreadyDeducted) {

                CouponRemainingSummary couponRemainingSummary = (CouponRemainingSummary) MessMationApplication.getCouponRemainingSummary();

                if(couponRemainingSummary!=null) {

                    String childName = "";
                    int remainingCoupons = -1;

                    if (mealName.equalsIgnoreCase(CouponTasks.BREAKFAST)) {
                        childName = "breakFastRemainingCoupons";
                        remainingCoupons = couponRemainingSummary.getBreakFastRemainingCoupons();
                        remainingCoupons = remainingCoupons - 1;
                    } else if (mealName.equalsIgnoreCase(CouponTasks.LUNCH)) {
                        childName = "lunchRemainingCoupons";
                        remainingCoupons = couponRemainingSummary.getLunchRemainingCoupons();
                        remainingCoupons = remainingCoupons - 1;
                    } else if (mealName.equalsIgnoreCase(CouponTasks.EVENINGTEA)) {
                        childName = "eveningTeaRemainingCoupons";
                        remainingCoupons = couponRemainingSummary.getEveningTeaRemainingCoupons();
                        remainingCoupons = remainingCoupons - 1;
                    } else if (mealName.equalsIgnoreCase(CouponTasks.DINNER)) {
                        childName = "dinnerRemainingCoupons";
                        remainingCoupons = couponRemainingSummary.getEveningTeaRemainingCoupons();
                        remainingCoupons = remainingCoupons - 1;
                    }

                    CouponDebitHistory currentDayCouponDebits = MessMationApplication.getCurrentDayCouponDebits();

                    List<CouponDebitDetail> couponDebitDetailList;
                    if(currentDayCouponDebits==null){
                        couponDebitDetailList = new ArrayList<CouponDebitDetail>();
                    }else{
                        couponDebitDetailList = currentDayCouponDebits.getCouponDebitList();
                    }

                    couponDebitDetailList.add(couponDebitDetail);

                    mUserDatabaseReference.child("userCouponHistory").child(forMonth).child("couponDebitHistory").child(currentDateStr).setValue(couponDebitDetailList);

                    mUserDatabaseReference.child("userCouponHistory").child(forMonth).child("couponRemainingSummary").child(childName).setValue(remainingCoupons);

                    status = 0;

                }else{
                    Log.d(TAG,"CouponRemainingSummary is not available");
                    status = 2;
                }
            }
            else{
                Log.d(TAG,"Inside DBAdapter coupon already deducted for this meal");
                status = 1;
            }
        }

        return status;
    }
    public void logCouponNotificationAction(String uid, String mealName, String notifAction) {
        Log.d(TAG,"Inside logCouponNotificationAction method with "+uid+ " meal name "+mealName+ " notifAction "+notifAction);

        SimpleDateFormat sdfForMonth = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String currentDateStr = sdf.format(new Date());
        mUserDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid);

        String forMonth = sdfForMonth.format(new Date());

        if (mUserDatabaseReference != null) { //TODO check if uid not found
            Log.d(TAG, "Inside logCouponNotificationAction mUserDatabaseReference " + mUserDatabaseReference);

            CouponNotifDetail couponNotifDetail = new CouponNotifDetail(mealName, currentDateStr, sdfWithTime.format(new Date()),notifAction);

            Log.d(TAG, "MessMationApplication.getCurrentDayCouponDebits() " + MessMationApplication.getCurrentDayCouponNotifs());

            CouponNotifHistory currentDayCouponNotifs = (CouponNotifHistory) MessMationApplication.getCurrentDayCouponNotifs();

            List<CouponNotifDetail> couponNotifList;

            boolean addToList = true;

            boolean updateNotifStatus = false;

            if (currentDayCouponNotifs == null) {
                couponNotifList = new ArrayList<CouponNotifDetail>();
            } else {
                couponNotifList = currentDayCouponNotifs.getCouponNotifList();

                Object[] couponDebitDetailObjArr = couponNotifList.toArray();

                for(int i =0;i<couponDebitDetailObjArr.length;i++){

                    HashMap<String, String> couponDebitDetailObj = (HashMap)couponDebitDetailObjArr[i];

                    Log.d(TAG,"Inside couponNotifList "+couponDebitDetailObj);

                    if (mealName.equalsIgnoreCase(couponDebitDetailObj.get("mealName"))) {
                        addToList = false;

                        if(!notifAction.equalsIgnoreCase(couponDebitDetailObj.get("notifAction"))){
                            updateNotifStatus = true;
                            couponDebitDetailObj.put("notifAction",notifAction);
                            couponDebitDetailObj.put("notifGeneratedTime",sdfWithTime.format(new Date()));
                        }
                    }
                }
            }

            if (addToList) {
                couponNotifList.add(couponNotifDetail);
                mUserDatabaseReference.child("userCouponHistory").child(forMonth).child("couponNotifHistory").child(currentDateStr).setValue(couponNotifList);
            }

            if(updateNotifStatus){
                mUserDatabaseReference.child("userCouponHistory").child(forMonth).child("couponNotifHistory").child(currentDateStr).setValue(couponNotifList);
            }
        }
    }

    private FirebaseDatabase getFirebaseDatabasInstance() {
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = mFirebaseDatabase.getInstance();
        }
        return mFirebaseDatabase;
    }


    public void getRemainingCouponsDetails(final FirebaseCallback fbc, String uid) {
        SimpleDateFormat sdfForMonth = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String forMonth = sdfForMonth.format(new Date());

        DatabaseReference mConfigDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid).child("userCouponHistory").child(forMonth).child("couponRemainingSummary");
        Log.d(TAG, "Inside getRemianingCouponsDetails " + mConfigDatabaseReference);

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot.getValue(CouponRemainingSummary.class) onDataChange" + dataSnapshot.getValue());
                fbc.onCompletionOfFirebaseCall(dataSnapshot.getValue(CouponRemainingSummary.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w(TAG, "getRemianingCouponsDetails:onCancelled", databaseError.toException());
            }
        };
        mConfigDatabaseReference.addListenerForSingleValueEvent(configListener);

    }

    public void getCurrentMonthCouponCreditDetails(final FirebaseCallback fbc, String uid) {
        SimpleDateFormat sdfForMonth = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String forMonth = sdfForMonth.format(new Date());

        DatabaseReference mConfigDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid).child("userCouponHistory").child(forMonth);
        Log.d(TAG, "Inside getCurrentMonthCouponCreditDetails " + mConfigDatabaseReference);

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot.getValue(Config.class) onDataChange" + dataSnapshot.getValue());

                CouponCreditDetail couponCreditedForCurrentMonth = null;

                if (dataSnapshot.getChildrenCount() > 0) {
                    CouponCreditDetail couponCreditDetail = dataSnapshot.getValue(CouponCreditDetail.class);

                    couponCreditedForCurrentMonth = new CouponCreditDetail();
                    couponCreditedForCurrentMonth.setForMonth(couponCreditDetail.getForMonth());
                    couponCreditedForCurrentMonth.setTotalCreditedCouponCount(couponCreditDetail.getTotalCreditedCouponCount());
                    couponCreditedForCurrentMonth.setValidaityUpto(couponCreditDetail.getValidaityUpto());
                    couponCreditedForCurrentMonth.setDefaultingPriority(couponCreditDetail.getDefaultingPriority());
                    couponCreditedForCurrentMonth.setCouponSetAmount(couponCreditDetail.getCouponSetAmount());
                    couponCreditedForCurrentMonth.setTransactionID(couponCreditDetail.getTransactionID());
                    couponCreditedForCurrentMonth.setTransactionDate(couponCreditDetail.getTransactionDate());
                }

                fbc.onCompletionOfFirebaseCall(couponCreditedForCurrentMonth);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w(TAG, "getCurrentMonthCouponCreditDetails:onCancelled", databaseError.toException());
            }
        };
        mConfigDatabaseReference.addListenerForSingleValueEvent(configListener);
    }
    public void getCurrentDayCouponNotifDetails(final FirebaseCallback fbc, String uid) {

        SimpleDateFormat sdfForMonth = new SimpleDateFormat("yyyy-MM");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = sdf.format(new Date());

        String forMonth = sdfForMonth.format(new Date());

        DatabaseReference mConfigDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid).child("userCouponHistory").child(forMonth).child("couponNotifHistory").child(currentDateStr);
        Log.d(TAG, "Inside getCurrentDayNotifDetails " + mConfigDatabaseReference);

        ValueEventListener configListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot.getValue getCurrentDayNotifDetails " + dataSnapshot.getValue());

                CouponNotifHistory currentDayCouponNotifDetails = null;

                if (dataSnapshot.getChildrenCount() > 0) {
                    Log.d(TAG, "dataSnapshot.getChildrenCount getCurrentDayNotifDetails " + dataSnapshot.getChildrenCount());
                    currentDayCouponNotifDetails = new CouponNotifHistory();
                    currentDayCouponNotifDetails.setCouponNotifList((List) dataSnapshot.getValue());
                }

                fbc.onCompletionOfFirebaseCall(currentDayCouponNotifDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w(TAG, "getCurrentDayCouponNotifDetails:onCancelled", databaseError.toException());
            }
        };
        mConfigDatabaseReference.addListenerForSingleValueEvent(configListener);

    }
    public void getCurrentDayCouponDebitDetails(final FirebaseCallback fbc, String uid) {

        SimpleDateFormat sdfForMonth = new SimpleDateFormat("yyyy-MM");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String currentDateStr = sdf.format(new Date());

        String forMonth = sdfForMonth.format(new Date());

        DatabaseReference mConfigDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid).child("userCouponHistory").child(forMonth).child("couponDebitHistory").child(currentDateStr);
        Log.d(TAG, "Inside getAppConfiguration " + mConfigDatabaseReference);

        ValueEventListener configListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot.getValue getCurrentDayCouponDebitDetails " + dataSnapshot.getValue());

                CouponDebitHistory currentDayCouponDebitDetails = null;

                if (dataSnapshot.getChildrenCount() > 0) {
                    Log.d(TAG, "dataSnapshot.getChildrenCount getCurrentDayCouponDebitDetails " + dataSnapshot.getChildrenCount());
                    currentDayCouponDebitDetails = new CouponDebitHistory();
                    currentDayCouponDebitDetails.setCouponDebitList((List) dataSnapshot.getValue());
                }

                fbc.onCompletionOfFirebaseCall(currentDayCouponDebitDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w(TAG, "getCurrentDayCouponDebitDetails:onCancelled", databaseError.toException());
            }
        };
        mConfigDatabaseReference.addListenerForSingleValueEvent(configListener);

    }


    public void getAppConfiguration(final FirebaseCallback fbc) {

        DatabaseReference mConfigDatabaseReference = getFirebaseDatabasInstance().getReference().child("config");
        Log.d(TAG, "Inside getAppConfiguration " + mConfigDatabaseReference);

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot.getValue(Config.class) onChildAdded" + dataSnapshot.getValue());
                fbc.onCompletionOfFirebaseCall(dataSnapshot.getValue(Config.class));
                //config = dataSnapshot.getValue(Config.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w(TAG, "getConfig:onCancelled", databaseError.toException());
            }
        };
        mConfigDatabaseReference.addListenerForSingleValueEvent(configListener);
    }
    public void getVendor(final FirebaseCallback fbc, String vid)
    {
        Log.d("TAG","getVendor call");
        DatabaseReference m=getFirebaseDatabasInstance().getReference().child("vendor").child(vid);

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "dataSnapshot.getValue(Config.class) onDataChange" + dataSnapshot.getValue());
                fbc.onCompletionOfFirebaseCall(dataSnapshot.getValue(Vendor.class));
                //config = dataSnapshot.getValue(Config.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w("TAG", "getConfig:onCancelled", databaseError.toException());
            }
        };
        m.addListenerForSingleValueEvent(configListener);

    }
    public void getUser(final FirebaseCallback fbc, String uid) {
        Log.d(TAG, "Inside getUser uid " + uid);

        DatabaseReference mConfigDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(uid);

        Log.d(TAG, "Inside getUser " + mConfigDatabaseReference);

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot.getValue(Config.class) onDataChange" + dataSnapshot.getValue());
                fbc.onCompletionOfFirebaseCall(dataSnapshot.getValue(User.class));
                //config = dataSnapshot.getValue(Config.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Config failed, log a message
                Log.w(TAG, "getConfig:onCancelled", databaseError.toException());
            }
        };
        mConfigDatabaseReference.addListenerForSingleValueEvent(configListener);
    }
    public int[] fetchCouponHistory(final FirebaseCallback fbc, final Date from, final Date to)
    {
        mealCount[0] = 0;
        mealCount[1] = 0;
        mealCount[2] = 0;
        mealCount[3] = 0;
        Log.d(TAG,from.toString()+ " "+ to.toString());
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG,"before user");
//                            user = snapshot.getValue(User.class);
                            Log.d(TAG,"after user");
                            DataSnapshot snap=snapshot.child("userCouponHistory");
                            if(snap == null || !snap.hasChildren())
                                continue;
                            for( DataSnapshot snap1 :snap.getChildren())//for each userCouponHistory
                            {
                                Log.d(TAG,"onuser Coupon History "+snap1.getKey());
                                int fromMonth = from.getMonth()+1;
                                int fromYear = from.getYear()+1900;
                                int toMonth = to.getMonth()+1;
                                int toYear = to.getYear()+1900;
                                int count=0,counter=0;
                                StringBuffer sb = new StringBuffer();
                                Log.d(TAG,"going for while loop");
                                Log.d(TAG,"date "+fromMonth+" "+fromYear+" "+toMonth+" "+toYear);
                                while(fromYear<=toYear) {
                                    count=0;
                                    while(true) {
                                        count++;

                                        if(count>12 || fromMonth>12){
                                            break;
                                        }
                                        if (fromMonth > toMonth && fromYear==toYear)
                                        {
                                            break;
                                        }
                                        sb.append(fromYear+"-"+((fromMonth<10)?("0"+fromMonth):(fromMonth+"")));
                                        //Code for fetching data from database
                                        Log.d(TAG,sb.toString());
                                        DataSnapshot snapshot2 = (snap1.getKey().equalsIgnoreCase(sb.toString()))? snap1 : null;
                                        if(snapshot2 == null) {
                                            fromMonth++;
                                            sb.delete(0, sb.length());
                                            continue;
                                        }
                                        DataSnapshot snapshot3 = snapshot2.child("couponDebitHistory");
                                        if(snapshot3 == null) {
                                            fromMonth++;
                                            sb.delete(0, sb.length());
                                            continue;
                                        }
                                        Log.d(TAG,"going inside Coupon History");
                                        for(DataSnapshot snap2:snapshot3.getChildren()) { //inside userCouponHistory > 2018-10 > couponDebitHistory > 2018-10-16
                                            try {
                                                Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(snap2.getKey());
                                                Log.d(TAG,snap2.getKey()+" abhhh");
                                                if((from.before(currentDate) || from.equals(currentDate)) && (currentDate.equals(to) || currentDate.before(to))) {
                                                    for(DataSnapshot snap3 : snap2.getChildren()) {
//                                                        couponDebitDetail = snapshot.getValue(CouponDebitDetail.class);
//                                                        Log.d(TAG, couponDebitDetail.getMealName()+" hhh");
                                                        String mealName = (String)snap3.child("mealName").getValue();
                                                        Log.d(TAG,mealName+" 123");
                                                        switch(mealName)
                                                        {
                                                            case "Breakfast":
                                                                mealCount[0]++;
                                                                break;
                                                            case "Lunch":
                                                                Log.d(TAG,"Lunch mein a gaye");
                                                                mealCount[1]++;
                                                                break;
                                                            case "Evening Tea":
                                                                mealCount[2]++;
                                                                break;
                                                            case "Dinner":
                                                                mealCount[3]++;
                                                                break;
                                                        }
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        sb.delete(0, sb.length());
                                        fromMonth++;
                                    }
                                    fromMonth=1;
                                    fromYear++;
                                }
                            }
                            Log.d(TAG,"after while loop");
                        }
                        String s = mealCount[0]+" "+mealCount[1]+" "+mealCount[2]+" "+mealCount[3];
                        fbc.onCompletionOfFirebaseCall(s);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        return mealCount;
    }
    public int[] fetchNotificationHistory(final FirebaseCallback fbc, final Date from, final Date to)
    {
        mealCount[0] = 0;
        mealCount[1] = 0;
        mealCount[2] = 0;
        mealCount[3] = 0;
        Log.d(TAG,from.toString()+ " "+ to.toString());
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG,"in on data change user");
                            DataSnapshot snap=snapshot.child("userCouponHistory");
                            if(snap == null || !snap.hasChildren())
                                continue;
                            for( DataSnapshot snap1 :snap.getChildren())//for each userCouponHistory
                            {
                                Log.d(TAG,"onuser Coupon History "+snap1.getKey());
                                int fromMonth = from.getMonth()+1;
                                int fromYear = from.getYear()+1900;
                                int toMonth = to.getMonth()+1;
                                int toYear = to.getYear()+1900;
                                int count=0,counter=0;
                                StringBuffer sb = new StringBuffer();
                                Log.d(TAG,"going for while loop");
                                Log.d(TAG,"date "+fromMonth+" "+fromYear+" "+toMonth+" "+toYear);
                                while(fromYear<=toYear) {
                                    count=0;
                                    while(true) {
                                        count++;
                                        if(count>12 || fromMonth>12){
                                            break;
                                        }
                                        if (fromMonth > toMonth && fromYear==toYear)
                                        {
                                            break;
                                        }
                                        sb.append(fromYear+"-"+((fromMonth<10)?("0"+fromMonth):(fromMonth+"")));
                                        //Code for fetching data from database
                                        Log.d(TAG,sb.toString());
                                        DataSnapshot snapshot2 = (snap1.getKey().equalsIgnoreCase(sb.toString()))? snap1 : null;
                                        if(snapshot2 == null) {
                                            fromMonth++;
                                            sb.delete(0, sb.length());
                                            continue;   //Cause of problem
                                        }
                                        DataSnapshot snapshot3 = snapshot2.child("couponNotifHistory");
                                        if(snapshot3 == null) {
                                            fromMonth++;
                                            sb.delete(0, sb.length());
                                            continue;
                                        }
                                        Log.d(TAG,"going inside CouponNotifHistory");
                                        for(DataSnapshot snap2:snapshot3.getChildren()) { //inside userCouponHistory > 2018-10 > couponNotifHistory > 2018-10-16
                                            try {
                                                Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(snap2.getKey());
                                                Log.d(TAG,snap2.getKey()+" key in date form");
                                                if((from.before(currentDate) || from.equals(currentDate)) && (currentDate.equals(to) || currentDate.before(to))) {
                                                    for(DataSnapshot snap3 : snap2.getChildren()) {
                                                        String mealName = (String)snap3.child("mealName").getValue();
                                                        String notifAction = (String)snap3.child("notifAction").getValue();
                                                        Log.d(TAG,mealName+" 123");
                                                        if(notifAction.equalsIgnoreCase("Deducted")) {
                                                            continue;
                                                        }
                                                        switch(mealName)
                                                        {
                                                            case "Breakfast":
                                                                mealCount[0]++;
                                                                break;
                                                            case "Lunch":
                                                                Log.d(TAG,"In Lunch");
                                                                mealCount[1]++;
                                                                break;
                                                            case "Evening Tea":
                                                                mealCount[2]++;
                                                                break;
                                                            case "Dinner":
                                                                mealCount[3]++;
                                                                break;
                                                        }
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        sb.delete(0, sb.length());
                                        fromMonth++;
                                    }
                                    fromMonth=1;
                                    fromYear++;
                                }
                            }
                            Log.d(TAG,"after while loop");
                        }
                        String s = mealCount[0]+" "+mealCount[1]+" "+mealCount[2]+" "+mealCount[3];
                        fbc.onCompletionOfFirebaseCall(s);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        return mealCount;
    }
    public void fetchDefaulterHistory(final FirebaseCallback fbc, final String month)
    {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            counter = 0;
                            priority = -1;
                            Log.d(TAG,"in on data change user");
                            DataSnapshot snap1 = snapshot.child("userCouponHistory");
                            if(snap1 == null)
                                continue;
                            DataSnapshot snap2 = snap1.child(month);
                            if(snap2 == null) {
                                continue;
                            }
                            DataSnapshot snap3 = snap2.child("couponNotifHistory");
                            if(snap3 == null) {
                                continue;
                            }
                            for (DataSnapshot snapshot2 : snap3.getChildren()) {
                                for(DataSnapshot snapshot3 : snapshot2.getChildren()) {
                                    Log.d(TAG, snapshot3.getKey());
                                    String notifAction = snapshot3.child("notifAction").getValue().toString();
                                    if(notifAction.equalsIgnoreCase("Deducted"))
                                        continue;
                                    counter++;
                                }
                            }
                            Log.d(TAG,"after while loop");
                            Log.d(TAG,"counter="+counter);
                            if(counter > 15) {
                                priority = 3;
                            } else if(counter > 10)
                                priority = 2;
                            else if(counter > 5)
                                priority = 1;
                            else if(counter > 0)
                                priority = 0;
                            //Block to set priority
                            DataSnapshot priorSnap = snap2.child("defaultingPriority");
                            if(priorSnap == null || priorSnap.getValue() == null) {
                                continue;
                            }
                            String prior;
                            prior = priorSnap.getValue().toString();
                            int pri = Integer.parseInt(prior);
                            if(pri < priority) {
                                mUserDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(snapshot.getKey()).child("userCouponHistory").child(month).child("defaultingPriority");
                                mUserDatabaseReference.setValue(priority);
                            } else {
                                priority = pri;
                            }
                            if(priority != -1)
                                emailPriority += snapshot.child("emailId").getValue().toString() + String.format("%1$20s", priority)+",";
                            emailPriorityDataSet.add(emailPriority);
                        }
                        if(emailPriority.length() > 0)
                            emailPriority = emailPriority.substring(0, emailPriority.length()-1);
                        fbc.onCompletionOfFirebaseCall(emailPriority);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
           });
    }
    public void getDefaulterByEmailId(final FirebaseCallback fbc, final String emailId, final String month){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String snapEmail = snapshot.child("emailId").getValue().toString();
                            if(emailId.equalsIgnoreCase(snapEmail)) {
                                fullName = snapshot.child("fullName").getValue().toString();
                                DataSnapshot snap1 = snapshot.child("userCouponHistory");
                                if(snap1 == null) {
                                    namePriority = fullName+",";
                                    break;
                                }
                                DataSnapshot snap2 = snap1.child(month);
                                if(snap2 == null) {
                                    namePriority = fullName+",";
                                    break;
                                }
                                DataSnapshot snap3 = snap2.child("defaultingPriority");
                                if(snap3 == null || snap3.getValue() == null) {
                                    namePriority = fullName+",";
                                    break;
                                }
                                markPriority = Integer.parseInt(snap3.getValue().toString());
                                namePriority = fullName+","+markPriority;
                                break;
                            }
                        }
                        fbc.onCompletionOfFirebaseCall(namePriority);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    public void setPriority(final FirebaseCallback fbc, final String emailId, final String month, final String priority)
    {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String snapEmail = snapshot.child("emailId").getValue().toString();
                            if(emailId.equalsIgnoreCase(snapEmail)) {
                                DataSnapshot snap1 = snapshot.child("userCouponHistory");
                                if(snap1 == null) {
                                    break;
                                }
                                DataSnapshot snap2 = snap1.child(month);
                                if(snap2 == null) {
                                    break;
                                }
                                DataSnapshot snap3 = snap2.child("defaultingPriority");
                                if(snap3 == null || snap3.getValue() == null) {
                                    break;
                                }
                                mUserDatabaseReference = getFirebaseDatabasInstance().getReference().child("users").child(snapshot.getKey()).child("userCouponHistory").child(month).child("defaultingPriority");
                                mUserDatabaseReference.setValue(priority);
//                                markPriority = Integer.parseInt(snap3.);

                                break;
                            }
                        }
                        fbc.onCompletionOfFirebaseCall(priority);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}