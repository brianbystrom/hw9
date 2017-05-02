package com.example.brianbystrom.hw09;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CreateTripActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DEMO";
    private static final int PLACE_PICKER_REQUEST = 1;
    Button cancelBTN, saveBTN;
    EditText titleET, locationET, imageET;
    ImageView imageIV, mapIV;
    String title, location, image;
    ArrayList<String> trips;

    private Location loc = new Location(null, null,"INIT","INIT");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference userRef;
    private FirebaseUser user;

    private GoogleApiClient mGoogleApiClient;


    private User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        cancelBTN = (Button) findViewById(R.id.cancelBTN);
        saveBTN = (Button) findViewById(R.id.saveBTN);

        titleET = (EditText) findViewById(R.id.titleET);
        locationET = (EditText) findViewById(R.id.locationET);
        imageET = (EditText) findViewById(R.id.imageET);

        imageIV = (ImageView) findViewById(R.id.userIV);
        mapIV = (ImageView) findViewById(R.id.mapIV);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //fNameET.setText(user.getDisplayName());
                    //lNameET.setText(user.getEmail());
                    myRef = database.getReference("users").child(user.getUid());

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
                                Log.d("demo", snapshot.getValue(User.class).toString());
                                //userList.add(snapshot.getValue(User.class));
                                Log.d(TAG, snapshot.getValue(User.class).getfName());
                                currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                currentUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());
                                currentUser.setTripsID(snapshot.getValue(User.class).getTripsID());
                                currentUser.setKey(snapshot.getValue(User.class).getKey());



                                //setProfileFields(currentUser);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    finish();
                }
                // ...
            }
        };

        mapIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(CreateTripActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        imageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Picasso.with(CreateTripActivity.this).load(s.toString()).into(imageIV);
                } catch (RuntimeException e) {
                    Toast.makeText(CreateTripActivity.this, "Invalid link for location image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleET.getText().toString().equals("") || locationET.getText().toString().equals("") || imageET.getText().toString().equals("")){
                    Toast.makeText(CreateTripActivity.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                } else if(loc.getName().equals("INIT")) {
                    Toast.makeText(CreateTripActivity.this, "Please select a location from the map.", Toast.LENGTH_SHORT).show();
                } else {

                    title = titleET.getText().toString();
                    location = locationET.getText().toString();
                    image = imageET.getText().toString();

                    myRef = database.getReference("trips");
                    String key = myRef.child("trips").push().getKey();
                    ArrayList<Message> m = new ArrayList<Message>();
                    ArrayList<Location> l = new ArrayList<Location>();
                    Message msg = new Message("INIT", "INIT", "INIT", "INIT");
                    //Location loc = new Location("INIT", "INIT", "INIT");
                    m.add(msg);
                    l.add(loc);

                    Trip trip = new Trip(user.getUid(), title, loc, image, key, m, l);


                    myRef = database.getReference("trips").child(key);

                    trips = currentUser.getTripsID();
                    trips.add(key);
                    currentUser.setTripsID(trips);

                    userRef = database.getReference("users").child(user.getUid());
                    userRef.setValue(currentUser);

                    myRef.setValue(trip);
                    Toast.makeText(CreateTripActivity.this, "Trip successfully created.", Toast.LENGTH_SHORT).show();
                    finish();


                }

            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                //String toastMsg = String.format("Place: %s", place.getName());
                loc.setLatitude(place.getLatLng().latitude);
                loc.setLongitude(place.getLatLng().longitude);
                loc.setName(place.getName().toString());
                locationET.setText(place.getName());
                //imageET.setText(place.getAttributions());



            }
        }}




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
