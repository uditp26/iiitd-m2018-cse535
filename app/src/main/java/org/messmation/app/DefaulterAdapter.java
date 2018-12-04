package org.messmation.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

public class DefaulterAdapter extends RecyclerView.Adapter<DefaulterAdapter.MyViewHolder> {
    private ArrayList<String> studentPriorityDataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dView;
        public MyViewHolder(View v) {
            super(v);
            //dView = v;
            dView=v.findViewById(R.id.emailIdPriority);//htana hai
        }
    }

    public DefaulterAdapter(ArrayList<String> studentPriorityDataSet) {
        this.studentPriorityDataSet = studentPriorityDataSet;
    }

    @Override
    public DefaulterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.defaulter_view, parent, false);
//        MyViewHolder vh = new MyViewHolder((TextView) v.findViewById(R.id.emailIdPriority));
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        TextView emailIdPriority = holder.dView.findViewById(R.id.emailIdPriority);
        holder.dView.setText(studentPriorityDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return studentPriorityDataSet.size();
    }
}