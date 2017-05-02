package com.example.brianbystrom.hw09;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FriendsTripActivity extends AppCompatActivity {

    private ArrayList<User> mDataset = new ArrayList<User>();

    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2;
    private FirebaseUser user;
    private User currentUser = new User();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uID;
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<String> fID = new ArrayList<String>();
    private ArrayList<Trip> tripList = new ArrayList<Trip>();
    private RecyclerView friendsTripsRV;
    private String uid;
    private TextView itemsTV;
    private ProgressBar PB;

    //final ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_trip);

        friendsTripsRV = (RecyclerView) findViewById(R.id.friendsTripsRV);
        itemsTV = (TextView) findViewById(R.id.itemsTV);
        PB = (ProgressBar) findViewById(R.id.progressBar);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //welcomeTV.setText("Welcome back " + user.getDisplayName());
                    myRef = database.getReference("users").child(user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
//                                Log.d("demo", snapshot.getValue(User.class).toString());
                                //Log.d(TAG, snapshot.getValue(User.class).getfName());
                                currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                currentUser.setFriendsUID((ArrayList<String>) snapshot.getValue(User.class).getFriendsUID());
                                currentUser.setTripsID((ArrayList<String>) snapshot.getValue(User.class).getTripsID());
                                //welcomeTV.setText("Welcome back " + currentUser.getfName() + " " + currentUser.getlName());

                                myRef2 = database.getReference("trips");

                                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {

                                        tripList.clear();
                                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                        for(DataSnapshot child: children) {
                                            Trip trip = child.getValue(Trip.class);
                                            //Log.d("COMPARE", user.getUid() + " | " + request.getuID());
                                            if(currentUser.getFriendsUID().contains(trip.getuID()) && !currentUser.getTripsID().contains(trip.gettID())) {
                                                tripList.add(trip);
                                                Log.d("ADDED", "ADDED" + trip.getTitle());
                                                updateCount(tripList.size());

                                            }
                                        }

                                        updateCount(tripList.size());

                                        Log.d("USER IDIDID", uid + "");

                                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(FriendsTripActivity.this, LinearLayoutManager.VERTICAL, false);
                                        FriendsTripAdapter mAdapter = new FriendsTripAdapter(tripList, FriendsTripActivity.this, uid);
                                        //mAdapter.notifyDataSetChanged();
                                        friendsTripsRV.setAdapter(mAdapter);
                                        friendsTripsRV.setLayoutManager(mLayoutManager);
                                        friendsTripsRV.setHasFixedSize(true);

                                        showItems();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });
                            }






                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void end(){
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void updateCount(int count) {
        if(count == 0) {
            itemsTV.setText("There are no trips of your friends that you are not added on.");
        } else {
            itemsTV.setText("Found " + count + " trips.");
        }
    }

    public void showItems() {
        PB.setVisibility(View.GONE);
        itemsTV.setVisibility(View.VISIBLE);
        friendsTripsRV.setVisibility(View.VISIBLE);
    }
}
