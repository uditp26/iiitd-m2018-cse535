package org.messmation.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;
import org.messmation.app.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CouponHistory extends Fragment {
    private static final String TAG = DBAdapter.class.getSimpleName();
    Date d1, d2;
    public CouponHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coupon_history, container, false);
        Button fetchBtn;
        final EditText fromDate=view.findViewById(R.id.fromDate);
        final EditText toDate=view.findViewById(R.id.toDate);

        final TextView breakfast,lunch,evening_tea,dinner;
        breakfast=view.findViewById(R.id.breakfast_coupon_count);
        lunch=view.findViewById(R.id.lunch_coupon_count);
        evening_tea=view.findViewById(R.id.evening_coupon_count);
        dinner=view.findViewById(R.id.dinner_coupon_count);

        fetchBtn = view.findViewById(R.id.get_coupon_history);

        fetchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String date1 = fromDate.getText().toString();
                String date2 = toDate.getText().toString();
                if(date1 == null || date1.equals("") || date2 == null || date2.equals("")) {
                    Toast.makeText(getActivity(), "Date not entered", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    d1 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
                    d2 = new SimpleDateFormat("yyyy-MM-dd").parse(date2);
                    if(d1.after(d2)) {
                        Toast.makeText(getActivity(), "Wrong Date Entered", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getActivity(), "Wrong Date Entered", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Parsing Error in DBAdapter date in fetching coupon for vendor");
                }
                Log.d(TAG,"in Coupon History Button");
                new DBAdapter().fetchCouponHistory(new FirebaseCallback() {
                    @Override
                    public void onCompletionOfFirebaseCall(Object obj) {
                        String c = (String)obj;
                        if(c == null) {
                            return;
                        }
                        String mealCount[] = c.split(" ");
                        breakfast.setText(mealCount[0]);
                        lunch.setText(mealCount[1]);
                        evening_tea.setText(mealCount[2]);
                        dinner.setText(mealCount[3]);
                    }
                } ,d1, d2);
            }
        });
        return view;
    }
}
