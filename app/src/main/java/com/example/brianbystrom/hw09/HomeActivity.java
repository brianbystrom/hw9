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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "DEMO2";
    private EditText fNameET, lNameET, emailET, passwordET, cPasswordET;
    private TextView welcomeTV;
    private Button editBTN;
    private String fName, lName, email, password, cPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef, userRef;
    private FirebaseUser user;
    private User currentUser = new User();
    private String uid;
    private RecyclerView tripsRV;
    private Button createTripBTN, friendsTripBTN;
    private ArrayList<Trip> tripList = new ArrayList<Trip>();
    private int d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        welcomeTV = (TextView) findViewById(R.id.welcomeTV);
        createTripBTN = (Button) findViewById(R.id.createTripBTN);
        friendsTripBTN = (Button) findViewById(R.id.friendsTripsBTN);

        tripsRV = (RecyclerView) findViewById(R.id.tripsRV);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
//        editBTN =(Button) findViewById(R.id.btn);
//        editBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),EditProfileActivity.class);
//                i.putExtra("UID",user.getUid());
//                startActivity(i);
//            }
//        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //welcomeTV.setText("Welcome back " + user.getDisplayName());
                    myRef = database.getReference("users").child(user.getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
//                                Log.d("demo", snapshot.getValue(User.class).toString());
                                Log.d(TAG, snapshot.getValue(User.class).getfName());
                                currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                currentUser.setFriendsUID((ArrayList<String>) snapshot.getValue(User.class).getFriendsUID());
                                currentUser.setTripsID((ArrayList<String>) snapshot.getValue(User.class).getTripsID());
                                welcomeTV.setText("Welcome back " + currentUser.getfName() + " " + currentUser.getlName());
                            }

                            /*userRef = database.getReference("users").child(user.getUid());

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {

                                    currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                    currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                    currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                    currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                    currentUser.setFriendsUID((ArrayList<String>) snapshot.getValue(User.class).getFriendsUID());
                                    currentUser.setTripsID((ArrayList<String>) snapshot.getValue(User.class).getTripsID());
                                    welcomeTV.setText("Welcome back " + user.getDisplayName());

                                    //Log.d("LENGTH", currentUser.getTripsID().get(1) + "");



                                    //getTrips(currentUser);

                                    //}
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });*/

                            getTrips(currentUser);


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

        createTripBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateTripActivity.class));
            }
        });

        friendsTripBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FriendsTripActivity.class));
            }
        });

    }


    public void fillRV(final ArrayList<Trip> t) {
        Log.d("T SIZE", t.size() + "");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
        TripAdapter mAdapter = new TripAdapter(t, HomeActivity.this);
        mAdapter.notifyDataSetChanged();
        tripsRV.setAdapter(mAdapter);
        tripsRV.setLayoutManager(mLayoutManager);
        tripsRV.setHasFixedSize(true);

        ItemClickSupport.addTo(tripsRV).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Trip trip = t.get(position);

                //MyAdapter.aTask.cancel(true);

                Intent toTripActivity = new Intent(HomeActivity.this, TripActivity.class);
                toTripActivity.putExtra("TRIP ID", trip.gettID());
                Log.d("TRIP ID", trip.gettID() + "");
                startActivity(toTripActivity);
            }
        });
    }
    public void getTrips(final User user) {

        final int c = user.getTripsID().size();

        if(c > 1) {



            for (int i = 0; i < user.getTripsID().size(); i++) {

                d = i;

                if (!user.getTripsID().get(i).toString().equals("DEBUG")) {
                    //myRef = database.getReference("trips").child(user.getTripsID().get(i).toString());
                    myRef = database.getReference("trips");

                    Log.d("TRIP ID", user.getTripsID().get(i).toString());
                    myRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
                            //Trip trip = new Trip();
                            /*trip.setTitle(snapshot.getValue(Trip.class).getTitle().toString());
                            trip.setLocation(snapshot.getValue(Trip.class).getLocation().toString());
                            trip.setImage(snapshot.getValue(Trip.class).getImage().toString());
                            trip.setuID("DEBUG");
                            trip.settID(snapshot.getValue(Trip.class).gettID().toString());*/

                            tripList.clear();

                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                            for(DataSnapshot child: children) {
                                Trip trip = child.getValue(Trip.class);
                                if(currentUser.getTripsID().contains(trip.gettID())) {
                                    tripList.add(trip);
                                }
                            }

                            /*boolean add = true;

                            for (int i = 0; i < tripList.size(); i++) {
                                if (tripList.get(i).gettID().toString().equals(trip.gettID().toString())) {
                                    add = false;
                                }
                            }

                            if (add) {
                                tripList.add(trip);
                            }

                            Log.d("SIZE TL", tripList.size() + "");*/

                            //Log.d("C | D", c + "|" + d);
                            //if (d == c - 1) {
                                Log.d("PRE FILL", tripList.size() + "");
                                fillRV(tripList);
                            //}


                            //}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }

            }
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profileMI:
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                return true;
            case R.id.friendsMI:
                Intent i = new Intent(HomeActivity.this, FriendsActivity.class);
                i.putExtra("UID",uid);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
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

}
