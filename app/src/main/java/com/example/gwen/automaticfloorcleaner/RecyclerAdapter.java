package com.example.gwen.automaticfloorcleaner;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gwen on 11/13/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    ArrayList<Room> arrayList = new ArrayList<>();

    public RecyclerAdapter(ArrayList<Room> arrayList){

        this.arrayList = arrayList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.room_name.setText(arrayList.get(position).getRoom_name());       //assign values to the textViews

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView room_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            room_name = (TextView)itemView.findViewById(R.id.txtRoom_name);             //txtRoom_name from row_item.xml

        }
    }
}
