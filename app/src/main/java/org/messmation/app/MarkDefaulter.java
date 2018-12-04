package org.messmation.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

import java.util.ArrayList;
import java.util.Arrays;

public class MarkDefaulter extends Fragment {
    View view;
    EditText emailId,markMonth;
    EditText priorityEditText;
    TextView nameTxtview;
    ConstraintLayout setPriorityLayout;
    public MarkDefaulter() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mark_defaulter, container, false);
        setPriorityLayout = view.findViewById(R.id.setPriorityLayout);
        setPriorityLayout.setVisibility(View.INVISIBLE);
        Button searchBtn = view.findViewById(R.id.searchDefaulterBtn);
        nameTxtview = view.findViewById(R.id.nameTextView);
        priorityEditText = view.findViewById(R.id.priorityEditText);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId = view.findViewById(R.id.markEmailId);
                String emailIdStr = emailId.getText().toString();
                if(emailIdStr.equals("")) {
                    Toast.makeText(getActivity(), "Enter EmailId to search", Toast.LENGTH_LONG).show();
                    return;
                }
                markMonth = view.findViewById(R.id.markMonth);
                String markMonthStr = markMonth.getText().toString();
                if(markMonthStr.equals("")) {
                    Toast.makeText(getActivity(), "Enter month to search", Toast.LENGTH_LONG).show();
                    return;
                }
                new DBAdapter().getDefaulterByEmailId(new FirebaseCallback(){
                    @Override
                    public void onCompletionOfFirebaseCall(Object obj) {
                        String data = (String) obj;
                        if(data != null && !data.equalsIgnoreCase("")) {
                            String arr[] = data.split(",");
                            if(arr.length > 0) {
                                nameTxtview.setText(arr[0]);
                            }
                            if(arr.length == 2) {
                                priorityEditText.setText(arr[1]);
                            }
                            setPriorityLayout.setVisibility(View.VISIBLE);
                        }
                        Log.d("TAG" , "SM="+data);

                    }
                }, emailIdStr, markMonthStr);
            }
        });
        Button setPriorityBtn = view.findViewById(R.id.setPriorityBtn);
        setPriorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String priority_new=priorityEditText.getText().toString();
                String emailIdStr = emailId.getText().toString();
                String markMonthStr = markMonth.getText().toString();
                new DBAdapter().setPriority(new FirebaseCallback(){
                    @Override
                    public void onCompletionOfFirebaseCall(Object obj) {
                        String data = (String) obj;
                        if(data != null && !data.equalsIgnoreCase("")) {
                            Toast.makeText(getContext(),"Priority Updated",Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG" , "SM="+data);

                    }
                }, emailIdStr, markMonthStr,priority_new);
            }
        });
        return view;
    }
}
