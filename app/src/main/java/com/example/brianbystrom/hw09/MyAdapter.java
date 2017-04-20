package com.example.brianbystrom.hw09;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;




/* Group 02 - Lakshmi Sridhar, Swetha Adla
   Homework 07
   MyAdapter.java
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    public FriendsActivity activity;

    private ArrayList<Friend> mDataset = new ArrayList<Friend>();


    public MyAdapter(ArrayList<Friend> mDataset, FriendsActivity activity) {
        this.mDataset = mDataset;
        this.activity = activity;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView nameTV;
        public ImageView profileUrlIV;


        public ViewHolder(View itemView) {
            super(itemView);
            this.nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            this.profileUrlIV = (ImageView) itemView.findViewById(R.id.profileUrlIV);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Friend friend = mDataset.get(position);

        TextView tv = holder.nameTV;
        tv.setText(friend.getUser() + " " + friend.getUser());

        ImageView iv = holder.profileUrlIV;

        //Picasso.with(activity).load(friend.getUser()).into(iv);

    }


    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}

