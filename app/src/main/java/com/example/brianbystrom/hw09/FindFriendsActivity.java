package com.example.brianbystrom.hw09;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by msalad on 4/20/2017.
 */

public class FindFriendsActivity extends AppCompatActivity {
    ListView find_friends;

    private static final String TAG = "DEMO";
    private FirebaseAuth mAuth;
    private String uID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2, myRef3;
    private DatabaseReference loggedUserRef;
    private FirebaseUser user;
    //private CustomArrayAdapter customArrayAdapter;
    private RecyclerView findFriendsRV;
    char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVEWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    ArrayList<User> allUsers = new ArrayList<User>();
    ArrayList<String> allRequests = new ArrayList<String>();
    private String myname;
    private ValueEventListener listener;
    private User currentUser = new User();
    private TextView itemsTV;
    private ProgressBar PB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        //find_friends = (ListView) findViewById(R.id.findFriends);
        findFriendsRV = (RecyclerView) findViewById(R.id.findFriendsRV);
        itemsTV = (TextView) findViewById(R.id.itemsTV);
        PB = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        if(getIntent().getExtras()!=null){
         myname = getIntent().getStringExtra("MYNAME");
        }
        database = FirebaseDatabase.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                uID = user.getUid();
                Log.d("usera",user+"");
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myRef = database.getReference();
                    loggedUserRef = database.getReference();
                    Log.d("mopa", myRef.toString());
                    allUsers = new ArrayList<User>();

                    myRef3 = database.getReference("users").child(user.getUid());

                    myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            currentUser.setfName(dataSnapshot.getValue(User.class).getfName().toString());
                            currentUser.setlName(dataSnapshot.getValue(User.class).getlName().toString());
                            currentUser.setGender(dataSnapshot.getValue(User.class).getGender().toString());
                            currentUser.setProfileURL(dataSnapshot.getValue(User.class).getProfileURL().toString());
                            currentUser.setFriendsUID(dataSnapshot.getValue(User.class).getFriendsUID());
                            currentUser.setTripsID(dataSnapshot.getValue(User.class).getTripsID());
                            currentUser.setKey(dataSnapshot.getValue(User.class).getKey());

                            myRef2 = database.getReference("requests");

                            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                    for(DataSnapshot child: children) {
                                        Request request = child.getValue(Request.class);
                                        Log.d("COMPARE", user.getUid() + " | " + request.getuID());
                                        if(user.getUid().equals(request.getuID()) || user.getUid().equals(request.getfID())) {
                                            allRequests.add(request.getfID());
                                        }
                                    }





                                    //myRef.child("friends").orderByChild("user").equalTo(user.getUid());
                                    //myRef.child("all");
                                    Log.d("USER ID", user.getUid() + "");

                                    //Even though the Log has a reference to a friend object, i can't seem to pull information from it like I was when
                                    //Pulling the user in the EditProfileActivity

                                    myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                            for(DataSnapshot child: children) {
                                                User user = child.getValue(User.class);
                                                if(!allRequests.contains(user.getKey()) && !currentUser.getFriendsUID().contains(user.getKey())) {
                                                    allUsers.add(user);
                                                    updateCount(allRequests.size());

                                                }
                                            }

                                            updateCount(allUsers.size());

                                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(FindFriendsActivity.this, LinearLayoutManager.VERTICAL, false);
                                            FindFriendsAdapter mAdapter = new FindFriendsAdapter(allUsers, FindFriendsActivity.this, uID);
                                            //mAdapter.notifyDataSetChanged();
                                            findFriendsRV.setAdapter(mAdapter);
                                            findFriendsRV.setLayoutManager(mLayoutManager);
                                            findFriendsRV.setHasFixedSize(true);

                                            showItems();


                                            //customArrayAdapter = new CustomArrayAdapter(allUsers,getApplication(),database,user.getUid());
                                            //find_friends.setAdapter(customArrayAdapter);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    // User is signed out
                    Log.d("faail", "onAuthStateChanged:signed_out");
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
            itemsTV.setText("You are friends with everyone.");
        } else {
            itemsTV.setText("Found " + count + " friends to add.");
        }
    }

    public void showItems() {
        PB.setVisibility(View.GONE);
        itemsTV.setVisibility(View.VISIBLE);
        findFriendsRV.setVisibility(View.VISIBLE);
    }

}
