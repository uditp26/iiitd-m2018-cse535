package org.messmation.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.model.CouponCreditDetail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class buyCoupon extends Fragment {

    private static final String TAG = buyCoupon.class.getSimpleName();
    Intent intent;
    int couponCount;
    Calendar c;
    Context context;

    private RadioGroup mRadioGroupBuyCoupons;
    private Button mBtnBuyCoupon;
    private TextView mCouponAlreadyBoughtTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_buy_coupon, container, false);
        mRadioGroupBuyCoupons = view.findViewById(R.id.radio_group_buy_coupon);
        mBtnBuyCoupon = view.findViewById(R.id.btn_buy_coupon);
        mCouponAlreadyBoughtTV = view.findViewById(R.id.tv_cpn_already_bght);

        MessMationApplication.initializeAppData();

        //code to handle the click event of buy coupons
        Button buyCouponBtn = view.findViewById(R.id.btn_buy_coupon);
        buyCouponBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Buy Coupon button clicked");
                buyCoupons();

            }
        });

        CouponCreditDetail couponCreditDetail = MessMationApplication.getCouponCreditForMonth();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String currentMonth = sdf.format(new Date());

        if(couponCreditDetail!=null && currentMonth.equalsIgnoreCase(couponCreditDetail.getForMonth())){
            mBtnBuyCoupon.setVisibility(View.GONE);
            mCouponAlreadyBoughtTV.setVisibility(View.VISIBLE);
            String mCouponAlreadyBoughtTVText = String.valueOf(mCouponAlreadyBoughtTV.getText());
            mCouponAlreadyBoughtTV.setText(mCouponAlreadyBoughtTVText + couponCreditDetail.getTotalCreditedCouponCount());
        }

        return view;
    }

    private void buyCoupons() {

        int radioButtonID = mRadioGroupBuyCoupons.getCheckedRadioButtonId();

        Log.d(TAG, "radioButtonID "+radioButtonID);

        RadioButton radioButton = mRadioGroupBuyCoupons.findViewById(radioButtonID);

        if(radioButton!=null) {

            String selectedtext = (String) radioButton.getText();

            couponCount = 0;

            double couponPrice = 0.0;

            if (selectedtext.equalsIgnoreCase("20 Days")) {
                couponCount = 20;
                couponPrice = 1800;
            } else if (selectedtext.equalsIgnoreCase("25 Days")) {
                couponCount = 25;
                couponPrice = 2100;
            } else if (selectedtext.equalsIgnoreCase("30 Days")) {
                couponCount = 30;
                couponPrice = 2300;
            }


            c = Calendar.getInstance();
            c.setTime(new Date());
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

            if (couponCount != 0) {
                intent = new Intent(context, MerchantActivity.class);
                intent.putExtra("ccount", couponCount);
                intent.putExtra("couponPrice", couponPrice);
                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        }else{
            Log.d(TAG,"No radio button selected");
            Toast.makeText(context,"Please select coupon set",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            /*if(resultCode == getActivity().RESULT_OK){
                dbAdapter.creditCoupons(loggedInuserID, couponCount, new Date(), c.getTime());
                mRadioGroupBuyCoupons.clearCheck();
                /*Intent intent2 = new Intent(this, homepage.class);
                //intent2.putExtra("success", Boolean.TRUE);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                //Toast.makeText(this,"Coupon Bought Successfully!!",Toast.LENGTH_SHORT).show();
            }*/
            if (resultCode == getActivity().RESULT_CANCELED) {
                //Toast.makeText(this,"Purchase cancelled!",Toast.LENGTH_SHORT).show();
                mRadioGroupBuyCoupons.clearCheck();
                //display error/payment cancelled dialog
            }
        }
    }
}
