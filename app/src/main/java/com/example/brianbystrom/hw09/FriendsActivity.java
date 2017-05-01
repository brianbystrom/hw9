package com.example.brianbystrom.hw09;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "DEMO";
    private RecyclerView friendRV;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef, myRef2;
    private FirebaseUser user;
    private ArrayList<User> friendData;
    private final User currentUser = new User();
    private User userProfile = new User();
    private String uID;
    private Button findFriendsBtn;
    private Button reqBTN;
    private Button cancelBtn;
    private RecyclerView friendsRV;
    //private UserAdapter myAdapter;
    private ArrayList<User> myFriends;
    private TextView friendsCount;
    private int nFriends;
    private String MYNAME;
    private ArrayList<String> fID = new ArrayList<String>();
    //private Friend newFriend = new Friend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        //uid = getIntent().getExtras().getString("UID");
        //Log.d("mask",uid); works
        mAuth = FirebaseAuth.getInstance();
        myFriends = new ArrayList<>();
        friendsRV = (RecyclerView) findViewById(R.id.friendsRV);
        database = FirebaseDatabase.getInstance();

        friendsCount = (TextView) findViewById(R.id.friendsCount);
        //myAdapter = new UserAdapter(this,R.layout.friend,myFriends);
        //friends.setAdapter(myAdapter);
        /*Friend friend = new Friend("JR8XB36F07dAuqTp2XL98vXkYOn1", "JR8XB36F07dAuqTp2XL98vXkYOn1");

        DatabaseReference ref = database.getReference("friends");
        //String key = ref.child("friends").push().getKey();
        ref = database.getReference("friends").child("123");
        ref.setValue(friend);*/
        /*friendData = new ArrayList<User>();*/
        cancelBtn = (Button) findViewById(R.id.cancelBTN);
        findFriendsBtn = (Button) findViewById(R.id.findBTN);
        reqBTN = (Button) findViewById(R.id.reqBTN);



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myRef = database.getReference();
                    uID = user.getUid();
                    Log.d("mop", myRef.toString());

                    myRef = database.getReference("users").child(user.getUid());

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fID.clear();
                            fID = dataSnapshot.getValue(User.class).getFriendsUID();
                            //Log.d("SIZE F", fID.get(0) + " | " + fID.get(1));
                            fID.removeAll(Collections.singleton(null));
                            fID.remove(uID);
                            updateFriendsCount(fID.size());

                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(FriendsActivity.this, LinearLayoutManager.VERTICAL, false);
                            FriendAdapter mAdapter = new FriendAdapter(fID, FriendsActivity.this, uID);
                            //mAdapter.notifyDataSetChanged();
                            friendsRV.setAdapter(mAdapter);
                            friendsRV.setLayoutManager(mLayoutManager);
                            friendsRV.setHasFixedSize(true);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    findFriendsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(FriendsActivity.this, FindFriendsActivity.class);
                            i.putExtra("MYNAME", MYNAME);
                            startActivity(i);
                        }
                    });

                    reqBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(FriendsActivity.this, FriendRequestsActivity.class);
                            //i.putExtra("MYNAME",MYNAME);
                            startActivity(i);
                        }
                    });
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                }
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

    public void updateFriendsCount(int count) {
        friendsCount.setText("You have " + count + " friends");
    }
}
