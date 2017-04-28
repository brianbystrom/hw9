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


public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.ViewHolder> {
    public FindFriendsActivity activity;
    public TripActivity tActivity;

    private ArrayList<User> mDataset = new ArrayList<User>();

    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2;
    private FirebaseUser user;
    private User currentUser = new User();
    private String uID;
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<String> fID = new ArrayList<String>();
    //final ValueEventListener listener;


    public FindFriendsAdapter(ArrayList<User> mDataset, FindFriendsActivity activity, String uID) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.uID = uID;
        database = FirebaseDatabase.getInstance();

        myRef = database.getReference("users").child(uID);

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


                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



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
                .inflate(R.layout.find_friend, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = mDataset.get(position);
        Log.d("TRIP NAME", user.getfName() + " " + position);

        TextView tv = holder.nameTV;
        //TextView tv2 = holder.locationTV;
        tv.setText(user.getfName().toString() + " " + user.getlName().toString());
        //tv2.setText(trip.getLocation().toString());

        ImageView iv2 = holder.addIV;

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.remove(position);
                Log.d("POS", position + "");
                //myRef.removeEventListener(listener);
                myRef2 = database.getReference();
                String key = myRef2.child("requests").push().getKey();
                Request request = new Request(uID, user.getKey(), key);
                myRef2 = database.getReference("requests").child(key);

                myRef2.setValue(request);
                //fID = currentUser.getFriendsUID();
                //fID.add(user.getKey());
                //currentUser.setFriendsUID(fID);
                //myRef.setValue(currentUser);
                notifyDataSetChanged();

            }

        });

        ImageView iv = holder.imageIV;
        Picasso.with(activity).load(user.getProfileURL().toString()).into(iv);




    }

    @Override
    public int getItemCount () {
        Log.d("SIZE", mDataset.size() + "");
        return mDataset.size();
    }


}
