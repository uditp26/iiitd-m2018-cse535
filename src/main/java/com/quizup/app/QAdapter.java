package com.quizup.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import java.util.Collections;

public class QAdapter extends RecyclerView.Adapter<QViewHolder> {

    private LayoutInflater inflater;
    private  Context context;
    List<Item> dataset = Collections.emptyList();

    public QAdapter(Context context, List<Item> dataset){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public QViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_row, viewGroup, false);
        Log.i("Adapter","onCreateViewHolder called.");
        QViewHolder viewHolder = new QViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QViewHolder viewHolder, int i) {
        Item item = dataset.get(i);
        Log.i("Adapter","onBindViewHolder called at pos "+i);
        viewHolder.question.setText(item.getQuestion());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /*public void deleteItem(int pos){
        dataset.remove(pos);
        notifyItemRemoved(pos);
    }*/

    /*public interface Mediator{
        public void handleClick(View view, int pos);
    }*/

    /*class QViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView question;
        ImageView img;
        Communicator com;

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
        }
    }*/
}
