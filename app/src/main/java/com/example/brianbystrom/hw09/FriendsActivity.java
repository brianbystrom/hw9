package com.example.brianbystrom.hw09;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    private ArrayList<Friend> friendData;
    private final User currentUser = new User();
    private User userProfile = new User();
    //private Friend newFriend = new Friend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        friendRV = (RecyclerView) findViewById(R.id.friendsRV);


        /*Friend friend = new Friend("JR8XB36F07dAuqTp2XL98vXkYOn1", "JR8XB36F07dAuqTp2XL98vXkYOn1");

        DatabaseReference ref = database.getReference("friends");
        //String key = ref.child("friends").push().getKey();
        ref = database.getReference("friends").child("123");
        ref.setValue(friend);*/



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef = database.getReference("friends");


                    //myRef.child("friends").orderByChild("user").equalTo(user.getUid());
                    myRef.child("friends");
                    Log.d("USER ID", user.getUid() + "");

                    //Even though the Log has a reference to a friend object, i can't seem to pull information from it like I was when
                    //Pulling the user in the EditProfileActivity
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
                                Log.d("demo", snapshot.getValue(Friend.class).toString());
                                //Log.d(TAG, snapshot.getValue(Friend.class).getUser());
                                Friend newFriend = new Friend();
                                newFriend.setUser(snapshot.getValue(Friend.class).getUser().toString());
                                newFriend.setFriend(snapshot.getValue(Friend.class).getFriend().toString());
                                friendData.add(newFriend);
                                /*//*currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());*/
                                Log.d("SIZE", friendData.size() +"");
                            }

                            setAdapter(friendData);
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

    public void setAdapter(ArrayList<Friend> f) {
        Log.d("SIZE", friendData.size() + "");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        MyAdapter mAdapter = new MyAdapter(f, FriendsActivity.this);
        mAdapter.notifyDataSetChanged();
        friendRV.setAdapter(mAdapter);
        friendRV.setLayoutManager(mLayoutManager);
        friendRV.setHasFixedSize(true);
    }
}
