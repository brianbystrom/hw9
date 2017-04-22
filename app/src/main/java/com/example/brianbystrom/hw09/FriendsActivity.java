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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    ListView friends;
    private UserAdapter myAdapter;
    private ArrayList<User> myFriends;
    private TextView friendsCount;
    private String MYNAME;
    //private Friend newFriend = new Friend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        uid = getIntent().getExtras().getString("UID");
        //Log.d("mask",uid); works
        mAuth = FirebaseAuth.getInstance();
        myFriends = new ArrayList<>();
        friends = (ListView) findViewById(R.id.a);
        database = FirebaseDatabase.getInstance();

        friendsCount = (TextView) findViewById(R.id.freindsCount);
        myAdapter = new UserAdapter(this,R.layout.friend,myFriends);
        friends.setAdapter(myAdapter);
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
                i.putExtra("MYNAME",MYNAME);
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
                    //myRef.child("users");
                    Log.d("USER ID", user.getUid() + "");

                    //Even though the Log has a reference to a friend object, i can't seem to pull information from it like I was when
                    //Pulling the user in the EditProfileActivity
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Log.d("mask ",snapshot.child(uid).child("friends").getChildrenCount()+"");
                            if((int)snapshot.child("users").child(uid).child("friendsUID").getChildrenCount()-1 < 0){
                                friendsCount.setText("Found "+0+" friends");

                            }
                            else {
                                friendsCount.setText("Found " + ((int) snapshot.child("users").child(uid).child("friendsUID").getChildrenCount()) + " friends");
                            }
                            MYNAME = snapshot.child("users").child(uid).child("fName").getValue(String.class);
                            if(Integer.parseInt(snapshot.child("users").child(uid).child("friendsUID").getChildrenCount()+"") > 0) {
                               // setAdapter(friendData);
                            }
                            myFriends.clear();
                            for(int i = 0; i < 100; i++){
                                if(snapshot.child("users").child(user.getUid()).child("friendsUID").child(i+"").exists() && !snapshot.child("users").child(user.getUid()).child("friendsUID").child(i+"").getValue(String.class).equals(user.getUid())){
                                    Log.d("do i have friends","yes");
                                    User u = new User();
                                    String friendsUid = snapshot.child("users").child(user.getUid()).child("friendsUID").child(i+"").getValue(String.class);
                                    u.setProfileURL(snapshot.child("users").child(friendsUid).child("profileURL").child(i+"").getValue(String.class));
                                    u.setfName(snapshot.child("users").child(friendsUid).child("fName").getValue(String.class));
                                    u.setlName(snapshot.child("users").child(friendsUid).child("lName").getValue(String.class));
                                    u.setGender(snapshot.child("users").child(friendsUid).child("gender").getValue(String.class));
                                    //Log.d("800",snapshot.child("users").child(friendsUid).child("fName").getValue(String.class)+"");
                                    myFriends.add(u);
                                }
                                updateList();
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

    cancelBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });
    }
    public void updateList(){
        myAdapter.notifyDataSetChanged();
        myAdapter.setNotifyOnChange(true);
    }
    public void removeFriend(char l){
        final char letter = l;
        final DatabaseReference temp = database.getReference();//.child(user.getUid());
        temp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(getApplicationContext(),dataSnapshot.child("all").child(letter+"")+"",Toast.LENGTH_LONG).show();

                if(dataSnapshot.child("all").child(letter+"").exists()){
                    String uidToRemove;
                    //Toast.makeText(getApplicationContext(),"close2",Toast.LENGTH_LONG).show();

                    uidToRemove = dataSnapshot.child("all").child(letter+"").getValue(String.class);
                    for(int i = 0; i < 100; i++){
                        Log.d("800","if " + uidToRemove + " equals " + dataSnapshot.child("users").child(user.getUid()).child("friendsUID").child(i+""));
                        if(uidToRemove.equals(dataSnapshot.child("users").child(user.getUid()).child("friendsUID").child(i+"").getValue(String.class))){
                            //Toast.makeText(getApplicationContext(),"close",Toast.LENGTH_LONG).show();

                            temp.child("users").child(user.getUid()).child("friendsUID").child(i+"").removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        updateList();
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

    public class UserAdapter extends ArrayAdapter<User> {

        Context ctx;
        int res;
        List<User> users;
        TextView textUserName;
        ImageView profileIMG;
        Button removeBtn;
        public UserAdapter(Context context, int resource, List<User> users) {
            super(context, resource);
            this.ctx = context;
            res = resource;
            this.users = users;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //Recycler view implementation
            //If convertView is null we are on our first item
            //User user = users.get(position);
            // Log.d("AM i being called?", users.get(position).getVendor());
            //if(convertView == null){
            //Create a LayoutInflator that will create our view
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(res,parent,false);
            //}

            textUserName = (TextView) convertView.findViewById(R.id.nameTV);
            profileIMG = (ImageView) convertView.findViewById(R.id.profileUrlIV);
            removeBtn = (Button) convertView.findViewById(R.id.addBtn);
            textUserName.setText(users.get(position).getfName() + " " + users.get(position).getlName());
            profileIMG.setImageResource(R.drawable.norm);
            removeBtn.setText("Delete");
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FriendsActivity) ctx).removeFriend(textUserName.getText().charAt(0));
                    //Toast.makeText(ctx,"delete",Toast.LENGTH_LONG).show();
                }
            });

            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            //Log.d("FOO", "item count: " + getCount());
        }

        @Override
        public int getCount(){
            return users!=null ? users.size() : 0;
        }
    }
}
