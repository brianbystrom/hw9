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


public class ViewPlacesAdapter extends RecyclerView.Adapter<ViewPlacesAdapter.ViewHolder> {
    public TripActivity activity;
    public TripActivity tActivity;

    private ArrayList<Location> mDataset = new ArrayList<Location>();

    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2, myRef3, myRef4;
    private FirebaseUser user;
    private User currentUser = new User();
    private User requestUser = new User();
    private User loggedInUser = new User();
    private String tID;
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<String> fID = new ArrayList<String>();
    private ArrayList<String> fID2 = new ArrayList<String>();
    //final ValueEventListener listener;


    public ViewPlacesAdapter(ArrayList<Location> mDataset, TripActivity activity, String tID) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.tID = tID;
        database = FirebaseDatabase.getInstance();




    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, locationTV;
        public ImageView imageIV, addIV;



        public ViewHolder(View itemView) {
            super(itemView);
            this.nameTV = (TextView) itemView.findViewById(R.id.nameTV);
            //this.locationTV = (TextView) itemView.findViewById(R.id.locationTV);
            this.imageIV = (ImageView) itemView.findViewById(R.id.imageIV);
            this.addIV = (ImageView) itemView.findViewById(R.id.addIV);


        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_places, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Location loc = mDataset.get(position);
        Log.d("FINAL UID", tID + "");


                TextView tv = holder.nameTV;
                //TextView tv2 = holder.locationTV;
                tv.setText(loc.getName());
                //tv2.setText(trip.getLocation().toString());

                ImageView iv2 = holder.addIV;

                iv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDataset.remove(position);

                        myRef = database.getReference("trips").child(tID);

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Trip trip = new Trip();

                                //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                                trip.setTitle(snapshot.getValue(Trip.class).getTitle().toString());
                                trip.setLocation(snapshot.getValue(Trip.class).getLocation());
                                trip.setImage(snapshot.getValue(Trip.class).getImage().toString());
                                trip.setuID(snapshot.getValue(Trip.class).getuID().toString());
                                trip.settID(snapshot.getValue(Trip.class).gettID().toString());
                                trip.setM(snapshot.getValue(Trip.class).getM());
                                trip.setL(mDataset);

                                myRef.setValue(trip);
                        notifyDataSetChanged();
                    }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });


                /*ImageView iv = holder.imageIV;
                Picasso.with(activity).load(currentUser.getProfileURL().toString()).into(iv);*/



                //}
            }


        });





    }

    @Override
    public int getItemCount () {
        Log.d("SIZE", mDataset.size() + "");
        return mDataset.size();
    }


}
