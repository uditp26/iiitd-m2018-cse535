package com.quizup.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class QViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView question;
    ImageView img;
    private Communicator com;

    public QViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        //itemView.setOnClickListener(this);
        com = (Communicator) context;
        question = itemView.findViewById(R.id.ques);
        img = itemView.findViewById(R.id.img);
        //question.setOnClickListener(this);
        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.i("ViewHolder","Item clicked at position "+getPosition());
        //Redirect to detail fragment
        if(com!=null){
            com.reactor(getPosition());
        }
        else{
            Log.e("Exception","Null pointer detected!");
        }
    }
}