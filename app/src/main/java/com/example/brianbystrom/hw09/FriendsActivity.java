package com.example.brianbystrom.hw09;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "DEMO";
    private RecyclerView friendRV;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private ArrayList<User> friendData;
    private final User currentUser = new User();
    private User userProfile = new User();
    private String uid;
    private Button findFriendsBtn;
    private Button cancelBtn;
    private TextView friendsCount;
    //private Friend newFriend = new Friend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        uid = getIntent().getExtras().getString("UID");
        //Log.d("mask",uid); works
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        friendRV = (RecyclerView) findViewById(R.id.friendsRV);
        friendsCount = (TextView) findViewById(R.id.freindsCount);

        /*Friend friend = new Friend("JR8XB36F07dAuqTp2XL98vXkYOn1", "JR8XB36F07dAuqTp2XL98vXkYOn1");

        DatabaseReference ref = database.getReference("friends");
        //String key = ref.child("friends").push().getKey();
        ref = database.getReference("friends").child("123");
        ref.setValue(friend);*/
        friendData = new ArrayList<User>();
        findFriendsBtn = (Button) findViewById(R.id.findBTN);
        cancelBtn = (Button) findViewById(R.id.cancelBTN);
        findFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FriendsActivity.this,FindFriendsActivity.class);
                startActivity(i);
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myRef = database.getReference();
                    Log.d("mop", myRef.toString());


                    //myRef.child("friends").orderByChild("user").equalTo(user.getUid());
                    myRef.child("users");
                    Log.d("USER ID", user.getUid() + "");

                    //Even though the Log has a reference to a friend object, i can't seem to pull information from it like I was when
                    //Pulling the user in the EditProfileActivity
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Log.d("mask ",snapshot.child(uid).child("friends").getChildrenCount()+"");
                            if(Integer.parseInt(snapshot.child("users").child(uid).child("friendsUID").getChildrenCount()+"") > 0) {
                                setAdapter(friendData);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void setAdapter(ArrayList<User> f) {
        Log.d("SIZE", friendData.size() + "");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        MyAdapter mAdapter = new MyAdapter(f, FriendsActivity.this);
        mAdapter.notifyDataSetChanged();
        friendRV.setAdapter(mAdapter);
        friendRV.setLayoutManager(mLayoutManager);
        friendRV.setHasFixedSize(true);
    }
}
