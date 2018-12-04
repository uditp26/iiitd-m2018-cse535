package org.messmation.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;

public class ManualCoupon extends Fragment{
    Context context;

    public ManualCoupon(){}

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

    private static final String TAG = ManualCoupon.class.getSimpleName();


    private TextView mCouponDateDay;
    private TextView mCouponDateMonth;
    private TextView mCouponDateYear;
    private TextView mMealName;
    private TextView mCouponStatus;

    private LinearLayout mCpnAvailableLL;
    private LinearLayout mCpnNotAvailableLL;

    private Button mManualCpnDedctBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_manual_coupon, container, false);
        mCouponDateDay = view.findViewById(R.id.tv_date_day);
        mCouponDateMonth = view.findViewById(R.id.tv_date_month);
        mCouponDateYear = view.findViewById(R.id.tv_date_year);
        mMealName = view.findViewById(R.id.tv_meal_name);
        mCouponStatus = view.findViewById(R.id.tv_coupon_status);

        mCpnNotAvailableLL = view.findViewById(R.id.cpn_nt_avlbl);
        mCpnAvailableLL = view.findViewById(R.id.cpn_avlbl);

        mManualCpnDedctBtn = view.findViewById(R.id.manualCpnDedctBtn);

        MessMationApplication.initializeAppData();

        String mealName = CouponTasks.determinCouponNameFromTime();

        Log.d(TAG,"Current mealName inside manual coupon deduction on create method  "+mealName);

        if(mealName==null){
            mCpnNotAvailableLL.setVisibility(View.VISIBLE);
            mCpnAvailableLL.setVisibility(View.GONE);
        }else{
            mCpnAvailableLL.setVisibility(View.VISIBLE);
            mCpnNotAvailableLL.setVisibility(View.GONE);

            mMealName.setText(mealName);

            Calendar cal = Calendar.getInstance();
            mCouponDateMonth.setText(new SimpleDateFormat("MMMM").format(cal.getTime()));
            mCouponDateYear.setText(new SimpleDateFormat("yyyy").format(cal.getTime()));
            mCouponDateDay.setText(new SimpleDateFormat("dd").format(cal.getTime()));

            boolean couponAlreadyDeducted = CouponTasks.checkIfCouponAlreadyDeductedForCurrentMeal(mealName);
            if(couponAlreadyDeducted){
                mCouponStatus.setText("Deducted");
                mCouponStatus.setVisibility(View.VISIBLE);
                mManualCpnDedctBtn.setVisibility(View.GONE);
            }else{
                mCouponStatus.setText("Not Deducted");
                mCouponStatus.setVisibility(View.VISIBLE);
                mManualCpnDedctBtn.setVisibility(View.VISIBLE);
            }
        }

        Button buyCouponBtn = view.findViewById(R.id.manualCpnDedctBtn);
        buyCouponBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Manual Coupon Deduction Clicked.");
                manualCouponDeductionAction();

            }
        });

        return view;
    }

    public void manualCouponDeductionAction() {
        int status = CouponTasks.executeTask(context,CouponTasks.ACTION_DEDUCT_COUPON);

        if(status==0){
            Toast.makeText(context,"Coupon deducted successfully",Toast.LENGTH_SHORT).show();
            mCouponStatus.setText("Deducted");
            mCouponStatus.setVisibility(View.VISIBLE);
            mManualCpnDedctBtn.setVisibility(View.GONE);
        }else if(status == 1){
            Toast.makeText(context,"Already deducted coupon for this meal",Toast.LENGTH_SHORT).show();
        }else if(status==2){
            Toast.makeText(context,"You don't have enough coupons to deduct",Toast.LENGTH_SHORT).show();
        }
    }
}
