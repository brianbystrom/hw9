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


public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {
    public FriendRequestsActivity activity;
    public TripActivity tActivity;

    private ArrayList<Request> mDataset = new ArrayList<Request>();

    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2, myRef3, myRef4;
    private FirebaseUser user;
    private User currentUser = new User();
    private User requestUser = new User();
    private User loggedInUser = new User();
    private String uID;
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<String> fID = new ArrayList<String>();
    private ArrayList<String> fID2 = new ArrayList<String>();
    //final ValueEventListener listener;


    public FriendRequestsAdapter(ArrayList<Request> mDataset, FriendRequestsActivity activity, String uID) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.uID = uID;
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
                .inflate(R.layout.req_friend, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Request request = mDataset.get(position);

        myRef = database.getReference("users").child(mDataset.get(position).getuID());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                Log.d("demo", snapshot.getValue(User.class).toString());
                userList.add(snapshot.getValue(User.class));
                //Log.d(TAG, snapshot.getValue(User.class).getfName());
                currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                currentUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());
                currentUser.setTripsID(snapshot.getValue(User.class).getTripsID());
                currentUser.setKey(snapshot.getValue(User.class).getKey());

                Log.d("TRIP NAME", currentUser.getfName() + " " + position);

                TextView tv = holder.nameTV;
                //TextView tv2 = holder.locationTV;
                tv.setText(currentUser.getfName().toString() + " " + currentUser.getlName().toString());
                //tv2.setText(trip.getLocation().toString());

                ImageView iv2 = holder.addIV;

                iv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDataset.remove(position);
                        Log.d("POS", position + "");
                        //myRef.removeEventListener(listener);
                        //Request request = new Request(uID, user.getKey());
                        myRef2 = database.getReference().child("requests");
                        myRef2.child(request.getKey()).removeValue();

                        myRef3 = database.getReference("users").child(uID);

                        myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                                Log.d("demo", snapshot.getValue(User.class).toString());
                                //userList.add(snapshot.getValue(User.class));
                                //Log.d(TAG, snapshot.getValue(User.class).getfName());
                                loggedInUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                loggedInUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                loggedInUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                loggedInUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                loggedInUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());
                                loggedInUser.setTripsID(snapshot.getValue(User.class).getTripsID());
                                loggedInUser.setKey(snapshot.getValue(User.class).getKey());

                                fID = loggedInUser.getFriendsUID();
                                fID.add(request.getuID());
                                loggedInUser.setFriendsUID(fID);
                                myRef3.setValue(loggedInUser);


                                //}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                        myRef4 = database.getReference("users").child(request.getuID());

                        myRef4.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                                Log.d("demo", snapshot.getValue(User.class).toString());
                                userList.add(snapshot.getValue(User.class));
                                //Log.d(TAG, snapshot.getValue(User.class).getfName());
                                requestUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                requestUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                requestUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                requestUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                requestUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());
                                requestUser.setTripsID(snapshot.getValue(User.class).getTripsID());
                                requestUser.setKey(snapshot.getValue(User.class).getKey());

                                fID2 = requestUser.getFriendsUID();
                                fID2.add(request.getfID());
                                requestUser.setFriendsUID(fID2);
                                myRef4.setValue(requestUser);


                                //}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });



                        notifyDataSetChanged();

                    }

                });

                ImageView iv = holder.imageIV;
                Picasso.with(activity).load(currentUser.getProfileURL().toString()).into(iv);



                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });





    }

    @Override
    public int getItemCount () {
        Log.d("SIZE", mDataset.size() + "");
        return mDataset.size();
    }


}
