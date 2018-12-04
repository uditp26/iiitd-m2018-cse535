package org.messmation.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.messmation.app.db.DBAdapter;
import org.messmation.app.db.FirebaseCallback;

import java.util.ArrayList;
import java.util.Arrays;


public class DefaulterHistory extends Fragment {
    private RecyclerView defaulterRecyclerView;
    private RecyclerView.Adapter dAdapter;
    private RecyclerView.LayoutManager dLayoutManager;
    ArrayList<String> studentEmailPriority;
    String monthwise;
    View view;
    public DefaulterHistory() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_defaulter_history, container, false);
        defaulterRecyclerView = (RecyclerView) view.findViewById(R.id.defaulterRecylerView);

        defaulterRecyclerView.setHasFixedSize(true);

        dLayoutManager = new LinearLayoutManager(getContext());

        Log.d("TAG", "as");
        Button btn = view.findViewById(R.id.getDefaulter);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText et = view.findViewById(R.id.monthwise);
                monthwise = et.getText().toString();
                Log.d("TAG","sa "+monthwise);
                if(monthwise == null || monthwise.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Month not entered correctly", Toast.LENGTH_LONG).show();
                    return;
                }
                new DBAdapter().fetchDefaulterHistory(new FirebaseCallback(){
                    @Override
                    public void onCompletionOfFirebaseCall(Object obj) {
                        defaulterRecyclerView.setLayoutManager(dLayoutManager);
                        String[] data = ((String) obj).split(",");
                        ArrayList<String> dataset = new ArrayList<>(Arrays.asList(data));
                        dAdapter = new DefaulterAdapter(dataset); //Get data from firebase
                        defaulterRecyclerView.setAdapter(dAdapter);
                    }
                }, monthwise);
            }
        });
        return view;
    }
}
