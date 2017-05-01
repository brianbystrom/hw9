package com.example.brianbystrom.hw09;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    public HomeActivity activity;
    public TripActivity tActivity;

    private ArrayList<Trip> mDataset = new ArrayList<Trip>();

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private User currentUser = new User();
    private String uid;


    public TripAdapter(ArrayList<Trip> mDataset, HomeActivity activity) {
        this.mDataset = mDataset;
        this.activity = activity;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV, locationTV;
        public ImageView imageIV;


        public ViewHolder(View itemView) {
            super(itemView);
            this.titleTV = (TextView) itemView.findViewById(R.id.userTV);
            this.locationTV = (TextView) itemView.findViewById(R.id.locationTV);
            this.imageIV = (ImageView) itemView.findViewById(R.id.userIV);


        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trips, parent, false);
        ViewHolder vh = new ViewHolder(v);



        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Trip trip = mDataset.get(position);
        Log.d("TRIP NAME", trip.getTitle() + " " + position);

        TextView tv = holder.titleTV;
        TextView tv2 = holder.locationTV;
        tv.setText(trip.getTitle().toString());
        tv2.setText(trip.getLocation().getName().toString());

        ImageView iv = holder.imageIV;
        Picasso.with(activity).load(trip.getImage().toString()).into(iv);




    }

    @Override
    public int getItemCount () {
        Log.d("SIZE", mDataset.size() + "");
        return mDataset.size();
    }


}
