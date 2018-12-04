package org.messmation.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.model.CouponRemainingSummary;

public class FragHomepage extends Fragment {

    private static final String TAG = FragHomepage.class.getSimpleName();
    private TextView mBreakFastPendingCount;
    private TextView mLunchPendingCount;
    private TextView mEveningTeaPendingCount;
    private TextView mDinnerPendingCount;
    Context context;
    private CouponRemainingSummary couponRemainingSummary;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        View view = inflater.inflate(R.layout.fragment_shomepage, container, false);
        mBreakFastPendingCount = view.findViewById(R.id.tv_breakfast_count);
        mLunchPendingCount = view.findViewById(R.id.tv_lunch_count);
        mEveningTeaPendingCount = view.findViewById(R.id.tv_eveningTea_count);
        mDinnerPendingCount = view.findViewById(R.id.tv_dinner_count);

        String loggedInUserID = MessMationApplication.getLoggedinUserUID();

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
        },loggedInUserID);

        return view;
    }

}
