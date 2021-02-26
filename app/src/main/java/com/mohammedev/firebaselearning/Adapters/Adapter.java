package com.mohammedev.firebaselearning.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammedev.firebaselearning.R;
import com.mohammedev.firebaselearning.data.MyData;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context context;
    MyData[] dataList;

    public Adapter(Context context, MyData[] dataList) {
        this.context = context;
        this.dataList = dataList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout , parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyData myData = dataList[position];

    }

    @Override
    public int getItemCount() {
        return dataList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
            private TextView childOne;
            private TextView childTwo;
            private TextView childThree;
            private TextView childFour;
            private TextView childFive;
            private TextView childSix;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
