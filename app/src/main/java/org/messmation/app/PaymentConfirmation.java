package org.messmation.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.model.CouponCreditDetail;
import org.messmation.app.model.CouponRemainingSummary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentConfirmation extends AppCompatActivity {

    private static final String TAG = PaymentConfirmation.class.getSimpleName();

    private TextView mPaymentStatus;
    private TextView mPaymentAmount;
    private TextView mPaymentTID;
    private TextView mPaymentTime;
    private TextView mPaymentCouponCount;
    private TextView mPaymentUserName;;
    private TextView mPaymentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        mPaymentStatus = findViewById(R.id.tv_payment_status);
        mPaymentAmount = findViewById(R.id.tv_payment_amount);
        mPaymentTID = findViewById(R.id.tv_payment_tid);
        mPaymentTime = findViewById(R.id.tv_payment_time);

        mPaymentCouponCount = findViewById(R.id.tv_coupon_count);

        mPaymentUserName = findViewById(R.id.tv_payment_user_name);
        mPaymentEmail = findViewById(R.id.tv_payment_user_email);

    }

    @Override
    protected void onStart() {
        super.onStart();

        String loggedInUserID = MessMationApplication.getLoggedinUserUID();


        Log.d(TAG, "Inside onStart method of homePage with "+loggedInUserID);

        new DBAdapter().getCurrentMonthCouponCreditDetails(new FirebaseCallback() {
            @Override
            public void onCompletionOfFirebaseCall(Object obj) {
                CouponCreditDetail couponCreditDetail = (CouponCreditDetail)obj;
                Log.d(TAG, "Data Retrieved couponCreditDetail " + couponCreditDetail);

                String userEmail = MessMationApplication.getLoggedinEmail();
                String userFullName = MessMationApplication.getLoggedinName();

                String transactionDateTime = "";

                if(couponCreditDetail!=null) {

                    SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                    SimpleDateFormat sdfWithTimeFormatted = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    try {
                      Date transDate = sdfWithTime.parse(couponCreditDetail.getTransactionDate());
                      transactionDateTime = sdfWithTimeFormatted.format(transDate);
                    }catch (Exception pe){
                        pe.printStackTrace();
                    }

                    mPaymentStatus.setText("Success");
                    mPaymentAmount.setText(String.valueOf(couponCreditDetail.getCouponSetAmount()));
                    mPaymentTID.setText(couponCreditDetail.getTransactionID());
                    mPaymentTime.setText(transactionDateTime);
                    mPaymentCouponCount.setText(String.valueOf(couponCreditDetail.getTotalCreditedCouponCount()));
                    mPaymentUserName.setText(userFullName);
                    mPaymentEmail.setText(userEmail);
                }
            }
        },loggedInUserID);
    }
}
