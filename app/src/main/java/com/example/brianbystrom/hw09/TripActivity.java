package com.example.brianbystrom.hw09;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Collections;

public class TripActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "TRIP ACT";
    private static final int PLACE_PICKER_REQUEST = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef, tripRef, myRef2, myRef3;
    private FirebaseUser user;
    private TextView titleTV, locationTV;
    private ImageView imageIV, addIV;
    private RecyclerView chatRV;
    private Trip trip = new Trip();
    private User currentUser = new User();
    private String uID;
    private Button sendMsgBTN;
    private EditText msgET;
    private String tripID;
    private ArrayList<User> friendsList = new ArrayList<User>();
    private ArrayList<String> fList = new ArrayList<String>();
    private ArrayList<Location> lList = new ArrayList<Location>();
    ArrayList<String> fList2 = new ArrayList<String>();
    private int count = 0;

    ChatAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        titleTV = (TextView) findViewById(R.id.userTV);
        locationTV = (TextView) findViewById(R.id.locationTV);

        imageIV = (ImageView) findViewById(R.id.userIV);
        addIV = (ImageView) findViewById(R.id.addIV);

        sendMsgBTN = (Button) findViewById(R.id.sendMsgBTN);
        msgET = (EditText) findViewById(R.id.msgET);

        chatRV = (RecyclerView) findViewById(R.id.chatRV);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();





        if(getIntent().getExtras() != null) {
            tripID = (String) getIntent().getExtras().getString("TRIP ID");
            //name_input.setText((String) editableMovie.name);

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        uID = user.getUid();
                        Log.d("UID DID ID", uID + "");

                        myRef = database.getReference("trips").child(tripID);

                        myRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {

                                trip.setTitle(snapshot.getValue(Trip.class).getTitle().toString());
                                trip.setLocation(snapshot.getValue(Trip.class).getLocation());
                                trip.setImage(snapshot.getValue(Trip.class).getImage().toString());
                                trip.setuID(snapshot.getValue(Trip.class).getuID().toString());
                                trip.settID(snapshot.getValue(Trip.class).gettID().toString());
                                trip.setM(snapshot.getValue(Trip.class).getM());
                                trip.setL(snapshot.getValue(Trip.class).getL());

                                boolean add = true;

                                titleTV.setText(trip.getTitle());
                                locationTV.setText(trip.getLocation().getName());
                                Picasso.with(TripActivity.this).load(trip.getImage().toString()).into(imageIV);
                                uID = user.getUid();
                                fillRV(trip.getM());
                                //}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                        myRef2 = database.getReference("users").child(user.getUid());

                        Log.d("UID", user.getUid() + "");

                        myRef2.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {

                                fList = dataSnapshot.getValue(User.class).getFriendsUID();


                                myRef3 = database.getReference("users");


                                myRef3.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {

                                        friendsList.clear();

                                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                        for(DataSnapshot child: children) {
                                            Log.d("HERE", "HERE");
                                            User user = child.getValue(User.class);
                                            if(currentUser.getTripsID().contains(trip.gettID())) {
                                                friendsList.add(user);
                                            }
                                        }
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
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };

            addIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RecyclerView friendsRV = new RecyclerView(TripActivity.this);
                    fList.removeAll(Collections.singleton(null));


                    for(int i =0; i < fList.size(); i++) {
                        myRef = database.getReference("users").child(fList.get(i));

                        count = i;
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                                Log.d("demo", snapshot.getValue(User.class).toString());
                                //Log.d(TAG, snapshot.getValue(User.class).getfName());
                                currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                currentUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());

                                ArrayList<String> temp = new ArrayList<String>();

                                temp = snapshot.getValue(User.class).getTripsID();

                                if(!temp.contains(tripID)) {
                                    fList2.add(fList.get(count));
                                }



                                //}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }


                    fList2.removeAll(Collections.singleton(null));

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(TripActivity.this, LinearLayoutManager.VERTICAL, false);
                    AddFriendTripAdapter mAdapter = new AddFriendTripAdapter(fList2, TripActivity.this, tripID);
                    //mAdapter.notifyDataSetChanged();
                    friendsRV.setAdapter(mAdapter);
                    friendsRV.setLayoutManager(mLayoutManager);
                    friendsRV.setHasFixedSize(true);


                    Dialog dialog = new Dialog(TripActivity.this);
                    dialog.setContentView(friendsRV);
                    dialog.setTitle("Add friends to trip");
                    //dialog.setMessage("Add friends to trip");
                    dialog.show();
                }
            });



            sendMsgBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(msgET.getText().toString().equals("")) {
                        Toast.makeText(TripActivity.this, "Please enter message.", Toast.LENGTH_SHORT).show();
                    } else {
                        Long tsLong = System.currentTimeMillis() / 1000;
                        String ts = tsLong.toString();
                        Log.d("ID", uID + "");
                        Message msg = new Message(uID, tripID, ts, msgET.getText().toString());
                        ArrayList<Message> mList = trip.getM();
                        mList.add(msg);
                        trip.setM(mList);

                        tripRef = database.getReference("trips").child(trip.gettID().toString());
                        tripRef.setValue(trip);
                        msgET.setText("");
                        //mAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
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

    public void fillRV(final ArrayList<Message> m) {
        Log.d("T SIZE", uID + "");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(TripActivity.this, LinearLayoutManager.VERTICAL, false);
        ChatAdapter mAdapter = new ChatAdapter(m, TripActivity.this, uID);
        mAdapter.notifyDataSetChanged();
        chatRV.setAdapter(mAdapter);
        chatRV.setLayoutManager(mLayoutManager);
        chatRV.setHasFixedSize(true);

        /*ItemClickSupport.addTo(chatRV).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Trip trip = t.get(position);

                //MyAdapter.aTask.cancel(true);

                Intent toTripActivity = new Intent(HomeActivity.this, TripActivity.class);
                toTripActivity.putExtra("TRIP ID", trip.gettID());
                Log.d("TRIP ID", trip.gettID() + "");
                startActivity(toTripActivity);
            }
        });*/
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.placeMI:
                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.viewMI:
                RecyclerView friendsRV = new RecyclerView(TripActivity.this);
                Log.d("SIZE SIZE", lList.size() + "");
                lList = trip.getL();
                lList.removeAll(Collections.singleton(null));
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(TripActivity.this, LinearLayoutManager.VERTICAL, false);
                ViewPlacesAdapter mAdapter = new ViewPlacesAdapter(lList, TripActivity.this, trip.gettID());
                //mAdapter.notifyDataSetChanged();
                friendsRV.setAdapter(mAdapter);
                friendsRV.setLayoutManager(mLayoutManager);
                friendsRV.setHasFixedSize(true);


                    /*AlertDialog dialog = new AlertDialog.Builder(TripActivity.this)
                            .setTitle("Add friends to trip.")

                            //.setMessage("Are you sure you want to delete this entry?")
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();*/
                Dialog dialog = new Dialog(TripActivity.this);
                dialog.setContentView(friendsRV);
                dialog.setTitle("Delete places from trip.");
                //dialog.setMessage("Add friends to trip");
                dialog.show();
                return true;
            case R.id.tripMI:
                double latitude = 40.714728;
                double longitude = -73.998672;
                String label = "ABC Label";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                showMap(uri);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Location loc = new Location(place.getLatLng().latitude, place.getLatLng().longitude, place.getName().toString(), "");
                lList = trip.getL();
                lList.add(loc);
                trip.setL(lList);
                myRef = database.getReference("trips").child(tripID);
                myRef.setValue(trip);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showMap(Uri geoLocation) {
        lList = trip.getL();
        Intent intent = new Intent(TripActivity.this, MapsActivity.class);
        intent.putExtra("MAP POINTS", lList);
        startActivity(intent);

    }
}
