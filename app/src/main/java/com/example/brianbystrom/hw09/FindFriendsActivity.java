package com.example.brianbystrom.hw09;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference loggedUserRef;
    private FirebaseUser user;
    private CustomArrayAdapter customArrayAdapter;
    char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVEWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    ArrayList<User> allUsers;
    private String myname;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        find_friends = (ListView) findViewById(R.id.findFriends);
        mAuth = FirebaseAuth.getInstance();
        if(getIntent().getExtras()!=null){
         myname = getIntent().getStringExtra("MYNAME");
        }
        database = FirebaseDatabase.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                Log.d("usera",user+"");
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myRef = database.getReference();
                    loggedUserRef = database.getReference();
                    Log.d("mopa", myRef.toString());
                   allUsers = new ArrayList<User>();

                    //myRef.child("friends").orderByChild("user").equalTo(user.getUid());
                    //myRef.child("all");
                    Log.d("USER ID", user.getUid() + "");
                    customArrayAdapter = new CustomArrayAdapter(allUsers,getApplication(),database,user.getUid());
                    find_friends.setAdapter(customArrayAdapter);
                    //Even though the Log has a reference to a friend object, i can't seem to pull information from it like I was when
                    //Pulling the user in the EditProfileActivity
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            allUsers.clear();
                            customArrayAdapter.notifyDataSetChanged();
                                for(int x = 0; x < alphabet.length; x++){
                                    //Log.d("test ",alphabet.length+"");
                                    Log.d("test ","if "+snapshot.child("all").child(alphabet[x]+"").getKey() +" doesnt equal "+myname.charAt(0)+"");

                                    if(snapshot.child("all").child(alphabet[x]+"").exists() && !snapshot.child("all").child(alphabet[x]+"").getKey().equals(myname.charAt(0)+"")) {
                                        User u = new User();
                                        u.setfName(snapshot.child("users").child(snapshot.child("all").child(alphabet[x]+"").getValue()+"").child("fName").getValue(String.class));
                                        u.setlName(snapshot.child("users").child(snapshot.child("all").child(alphabet[x]+"").getValue()+"").child("lName").getValue(String.class));
                                        u.setGender(snapshot.child("users").child(snapshot.child("all").child(alphabet[x]+"").getValue()+"").child("gender").getValue(String.class));
                                        u.setProfileURL(snapshot.child("users").child(snapshot.child("all").child(alphabet[x]+"").getValue()+"").child("profileURL").getValue(String.class));
                                        allUsers.add(u);
                                        Log.d("AAA",snapshot.child("users").child(snapshot.child("all").child(alphabet[x]+"").getValue()+"")+"");
                                    }
                                    Log.d("size of list",allUsers.size()+"");
                                    customArrayAdapter.notifyDataSetChanged();


                                }

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

    public class CustomArrayAdapter extends ArrayAdapter<User>{
        ArrayList<User> mDataset;
        Context c;
        TextView name;
        Button add;
        ImageView img;
        FirebaseDatabase l;
String id;
        CustomArrayAdapter(ArrayList<User> dataSet, Context c, FirebaseDatabase lU, String Uid){
            super(c,R.layout.friend,dataSet);
            mDataset = dataSet;
            this.c = c;
            l = lU;
            id = Uid;

        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater v =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = v.inflate(R.layout.friend,parent,false);
            name = (TextView)convertView.findViewById(R.id.nameTV);
            add = (Button) convertView.findViewById(R.id.addBtn);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Add to arrayList
                    l.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> temp = new ArrayList();
                             //temp = dataSnapshot.child("users").child(id).child("friendsUID").child(dataSnapshot.child("users").child(id).child("friendsUID")+"").getValue(ArrayList.class);
                            for(int i = 0; i < dataSnapshot.child("users").child(id).child("friendsUID").getChildrenCount(); i++){
                                if(dataSnapshot.child("users").child(id).child("friendsUID").child(i+"").exists()){
                                temp.add(dataSnapshot.child("users").child(id).child("friendsUID").child(i+"").getValue(String.class));
                                }
                            }
                            Log.d("zone",dataSnapshot.child("users").child(id).child("friendsUID").getChildrenCount()+"");
                            temp.add(dataSnapshot.child("all").child(mDataset.get(position).getfName().charAt(0)+"").getValue(String.class));
                            l.getReference().child("users").child(id).child("friendsUID").setValue(temp);
//                            ((FindFriendsActivity) c).end();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            });
            img = (ImageView) convertView.findViewById(R.id.profileUrlIV);
            img.setImageResource(R.drawable.norm);
            name.setText(mDataset.get(position).getfName() +" " +mDataset.get(position).getlName());
            add.setText("Add");
            return convertView;
        }

        @Override
        public int getCount() {
            return mDataset.size();
        }
    }
}
