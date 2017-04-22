package com.example.brianbystrom.hw09;

import android.content.Context;
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





public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    public Context c;

    private ArrayList<User> mDataset = new ArrayList<User>();


    public MyAdapter(ArrayList<User> mDataset, Context activity) {
        this.mDataset = mDataset;
        this.c = activity;

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
        final User friend = mDataset.get(position);

        TextView tv = holder.nameTV;
        tv.setText(friend.getfName() + " " + friend.getlName());

        ImageView iv = holder.profileUrlIV;

    }


    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}

