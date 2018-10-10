package com.quizup.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class QList extends Fragment {
    private RecyclerView recyclerView;
    private QAdapter adapter;
    Communicator com;

    public QList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        com = (Communicator)getActivity();
        Log.i("CommunicatorInfo", com.toString());
    }

    public Communicator getReference(){
        return com;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qlist, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        adapter = new QAdapter(getActivity(), fetchData());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
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

    public static List<Item> fetchData(){
        List<Item> data = new ArrayList<>();
        // fetch data from DB
        String[] questions ={"Question1", "Question2", "Question3", "Question4", "Question5","Question6", "Question7", "Question8", "Question9", "Question10", "Question11", "Question12", "Question13", "Question14", "Question15","Question16", "Question17", "Question18", "Question19", "Question20", "Question21", "Question22", "Question23", "Question24", "Question25", "Question26", "Question27", "Question28", "Question29", "Question30"};
        for(int i=0;i<30;i++){
            Item citem = new Item();
            citem.setQuestion(questions[i%questions.length]);
            data.add(citem);
        }
        return data;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

     /*class QViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView question;
        ImageView img;
        Communicator communicator;

        public QViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            question = itemView.findViewById(R.id.ques);
            img = itemView.findViewById(R.id.img);
            //question.setOnClickListener(this);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i("ViewHolder","Item clicked at position "+getPosition());
            //Redirect to detail fragment
            communicator = (Communicator) QList.this.getActivity();
            if(communicator!=null){
                communicator.reactor(getPosition());
            }
            else{
                Log.e("Exception","Null Pointer detected");
            }
        }
    }*/

}
