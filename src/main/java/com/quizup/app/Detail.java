package com.quizup.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class Detail extends Fragment implements View.OnClickListener{
    String question;
    Bundle bundle;
    TextView textView;
    Button submit, save, clear;
    RadioButton rbut1, rbut2;
    RadioGroup rgrp;
    Communicator com;
    View view;

    public Detail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com = (Communicator)getActivity();
        bundle = this.getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        rbut1 = view.findViewById(R.id.rb1);
        rbut2 = view.findViewById(R.id.rb2);
        /*if(savedInstanceState!=null){
            Log.i("SaveInstance ","Bundle restored.");
            if(savedInstanceState.getString("rbut1").equals("True")){
                //rbut1 = view.findViewById(R.id.rb1);
                rbut1.toggle();
            }
            else if(savedInstanceState.getString("rbut2").equals("False")){
                //rbut2 = view.findViewById(R.id.rb1);
                rbut2.toggle();
            }
        }*/
        question = bundle.getString("ques");
        textView = view.findViewById(R.id.textView1);
        submit = view.findViewById(R.id.submit);
        save = view.findViewById(R.id.save);
        clear = view.findViewById(R.id.clear);
        rgrp = view.findViewById(R.id.radiog);
        textView.setText(question);
        save.setOnClickListener(this);
        submit.setOnClickListener(this);
        clear.setOnClickListener(this);
        // retrieve data from database
        Boolean b = com.checker(bundle.getInt("pos"));
        if(b!=null){
            if(b){
                rbut1.toggle();
            }
            else{
                rbut2.toggle();
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i("SaveInstance ","OnSaveInstanceState called.");
        if(rbut1.isChecked())
            outState.putString("rbut1", "True");
        else if(rbut1.isChecked())
            outState.putString("rbut2", "False");
        super.onSaveInstanceState(outState);
    }*/

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.save){
            if(rbut1.isChecked()){       //rgrp.getChildAt(0).isActivated()
                com.updator(bundle.getInt("pos"), Boolean.TRUE);
                Toast.makeText(getContext(),"Answer saved!", Toast.LENGTH_SHORT).show();
            }
            else if(rbut2.isChecked()){                  //rgrp.getChildAt(1).isActivated()
                com.updator(bundle.getInt("pos"), Boolean.FALSE);
                Toast.makeText(getContext(),"Answer saved!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(),"No option selected! Answer not saved.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v.getId() == R.id.clear){
            rgrp.clearCheck();
        }
        else{
            com.exporter();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
