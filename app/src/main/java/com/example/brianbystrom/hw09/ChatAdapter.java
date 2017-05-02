package com.example.brianbystrom.hw09;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "CHAT ADAPTER";
    public TripActivity activity;

    private ArrayList<Message> mDataset = new ArrayList<Message>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef, tripRef, userRef;
    private FirebaseUser user;
    private String uID;
    private User msgUser = new User();


    public ChatAdapter(ArrayList<Message> mDataset, TripActivity activity, String uID) {
        //mDataset.remove(0);
        this.mDataset = mDataset;
        this.activity = activity;
        this.uID = uID;
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView userTV;
        public TextView msgTV;
        public TextView timeTV;
        public ImageView userIV;
        public ImageView deleteIV;


        public ViewHolder(View itemView) {
            super(itemView);
            this.userTV = (TextView) itemView.findViewById(R.id.userTV);
            this.msgTV = (TextView) itemView.findViewById(R.id.msgTV);
            this.timeTV = (TextView) itemView.findViewById(R.id.timeTV);
            this.userIV = (ImageView) itemView.findViewById(R.id.userIV);
            this.deleteIV = (ImageView) itemView.findViewById(R.id.deleteIV);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v;
        Log.d("VT", viewType + "");

        if(viewType == 1) {
            v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_you, parent, false);
        } else if(viewType == 0) {
            v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_them, parent, false);
        } else {
            v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_init, parent, false);
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Message message = mDataset.get(position);

        if(!message.getuID().equals("INIT")) {

            userRef = database.getReference("users").child(message.getuID());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {

                    msgUser.setfName(snapshot.getValue(User.class).getfName().toString());
                    msgUser.setlName(snapshot.getValue(User.class).getlName().toString());
                    msgUser.setGender(snapshot.getValue(User.class).getGender().toString());
                    msgUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                    msgUser.setFriendsUID((ArrayList<String>) snapshot.getValue(User.class).getFriendsUID());
                    msgUser.setTripsID((ArrayList<String>) snapshot.getValue(User.class).getTripsID());

                    //Log.d("LENGTH", currentUser.getTripsID().get(1) + "");


                    TextView tv = holder.userTV;
                    TextView tv2 = holder.msgTV;
                    TextView tv3 = holder.timeTV;

                    tv.setText(msgUser.getfName() + " " + msgUser.getlName());
                    tv2.setText(message.getMsg().toString());

                    try {
                        PrettyTime pt = new PrettyTime();
                        Long time = Long.parseLong(message.getTime()) *1000;
                        tv3.setText(pt.format(new Date(time)));
                    } catch (NumberFormatException e) {
                        tv3.setText(message.getTime());
                    }
                    ImageView iv = holder.userIV;
                    Picasso.with(activity).load(msgUser.getProfileURL()).into(iv);

                    ImageView iv2 = holder.deleteIV;

                    iv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDataset.remove(position);




                            notifyDataSetChanged();
                        }
                    });

                    //}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }


    }


    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        int you;

        Log.d("INIT", mDataset.get(position).getuID().toString() + " | " + uID);

        if(mDataset.get(position).getuID().toString().equals(uID)) {
            you = 1;
        } else if (mDataset.get(position).getuID().toString().equals("INIT")) {
            you = -1;
        } else {
            you = 0;
        }

        return you;
    }



}



