package com.example.protagonist26.mreverb;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Fragment1_A2_MT18049 extends Fragment implements View.OnClickListener{
    Mediator_A2_MT18049 med;
    Button play, stop;

    private OnFragmentInteractionListener mListener;

    public Fragment1_A2_MT18049() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        med = (Mediator_A2_MT18049) getActivity();
        play = getActivity().findViewById(R.id.b1);
        stop = getActivity().findViewById(R.id.b2);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment1__a2__mt18049, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.b1){
            med.react("BPlay");
        }
        else if(view.getId() == R.id.b2){
            med.react("BStop");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
