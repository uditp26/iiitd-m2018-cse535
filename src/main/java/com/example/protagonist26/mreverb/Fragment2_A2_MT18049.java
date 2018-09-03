package com.example.protagonist26.mreverb;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment2_A2_MT18049 extends Fragment implements  View.OnClickListener{
    Mediator_A2_MT18049 med;
    Button connect;

    private OnFragmentInteractionListener mListener;

    public Fragment2_A2_MT18049() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        med = (Mediator_A2_MT18049) getActivity();
        connect = getActivity().findViewById(R.id.b3);
        connect.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment2__a2__mt18049, container, false);
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
        if(view.getId() == R.id.b3){
            med.react("BConnect");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
