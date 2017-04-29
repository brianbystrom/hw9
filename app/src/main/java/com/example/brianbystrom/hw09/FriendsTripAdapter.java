package com.example.brianbystrom.hw09;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FriendsTripAdapter extends RecyclerView.Adapter<FriendsTripAdapter.ViewHolder> {
    public FriendsTripActivity activity;
    public TripActivity tActivity;

    private ArrayList<Trip> mDataset = new ArrayList<Trip>();

    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef3;
    private FirebaseUser user;
    private User currentUser = new User();
    private String uID;


    public FriendsTripAdapter(ArrayList<Trip> mDataset, FriendsTripActivity activity, String uID) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.uID = uID;
        database = FirebaseDatabase.getInstance();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV, locationTV;
        public ImageView imageIV, addIV;


        public ViewHolder(View itemView) {
            super(itemView);
            this.titleTV = (TextView) itemView.findViewById(R.id.userTV);
            this.locationTV = (TextView) itemView.findViewById(R.id.locationTV);
            this.imageIV = (ImageView) itemView.findViewById(R.id.userIV);
            this.addIV = (ImageView) itemView.findViewById(R.id.addIV);


        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_trips, parent, false);
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
        tv2.setText(trip.getLocation().toString());

        ImageView iv = holder.imageIV;
        Picasso.with(activity).load(trip.getImage().toString()).into(iv);

        ImageView iv2 = holder.addIV;
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Trip trip = mDataset.get(position);
                mDataset.remove(position);
                notifyDataSetChanged();
                Log.d("DEMOSDFDSFE######", uID);



                myRef3 = database.getReference("users").child(uID);

                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                        Log.d("demo", snapshot.getValue(User.class).toString());
                        User loggedInUser = new User();
                        ArrayList<String> tID = new ArrayList<String>();
                        //userList.add(snapshot.getValue(User.class));
                        //Log.d(TAG, snapshot.getValue(User.class).getfName());
                        loggedInUser.setfName(snapshot.getValue(User.class).getfName().toString());
                        loggedInUser.setlName(snapshot.getValue(User.class).getlName().toString());
                        loggedInUser.setGender(snapshot.getValue(User.class).getGender().toString());
                        loggedInUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                        loggedInUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());
                        loggedInUser.setTripsID(snapshot.getValue(User.class).getTripsID());
                        loggedInUser.setKey(snapshot.getValue(User.class).getKey());

                        tID = loggedInUser.getTripsID();
                        tID.add(trip.gettID());
                        loggedInUser.setTripsID(tID);
                        myRef3.setValue(loggedInUser);


                        //}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        });




    }

    @Override
    public int getItemCount () {
        Log.d("SIZE", mDataset.size() + "");
        return mDataset.size();
    }


}
