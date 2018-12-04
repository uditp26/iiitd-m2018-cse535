package org.messmation.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationHistory extends Fragment {
    private static final String TAG = NotificationHistory.class.getSimpleName();
    Date d1, d2;
    public NotificationHistory(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification_history, container, false);
        //Add Code to for the view
        Button fetchBtn;
        final EditText fromDate=view.findViewById(R.id.fromDateNotify);
        final EditText toDate=view.findViewById(R.id.toDateNotify);

        final TextView breakfast,lunch,evening_tea,dinner;
        breakfast=view.findViewById(R.id.breakfast_notification_count);
        lunch=view.findViewById(R.id.lunch_notification_count);
        evening_tea=view.findViewById(R.id.evening_notification_count);
        dinner=view.findViewById(R.id.dinner_notification_count);

        fetchBtn = view.findViewById(R.id.get_notification_history);

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
                    Log.d(TAG,"Parsing Error in DBAdapter date in fetching Notification for vendor");
                }
                Log.d(TAG,"in Notification History Button");
                new DBAdapter().fetchNotificationHistory(new FirebaseCallback() {
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
