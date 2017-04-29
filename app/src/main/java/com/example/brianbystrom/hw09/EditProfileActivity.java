package com.example.brianbystrom.hw09;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "DEMO";
    private EditText fNameET, lNameET, profileUrlET;
    private ImageView profileUrlIV;
    private Spinner genderSP;
    private Button cancelBTN, saveBTN;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private ArrayList<User> userData;
    private final User currentUser = new User();
    private ArrayList<String> fID, tID;
    private User userProfile = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        cancelBTN = (Button) findViewById(R.id.cancelBTN);
        saveBTN = (Button) findViewById(R.id.saveBTN);

        fNameET = (EditText) findViewById(R.id.fNameET);
        lNameET = (EditText) findViewById(R.id.lNameET);
        profileUrlET = (EditText) findViewById(R.id.profileUrlET);
        profileUrlIV = (ImageView) findViewById(R.id.imageIV);
        genderSP = (Spinner) findViewById(R.id.genderSP);


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

                    getUserProfile(user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Log.d(TAG, user.getUid() + " asdf");

        /*myRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (com.google.firebase.database.DataSnapshot snapshot: snapshot.getChildren()) {
                    Log.d("demo", snapshot.getValue(User.class).toString());
                    userList.add(snapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });*/





        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName, lName, gender, profileURL;

                fName = fNameET.getText().toString();
                lName = lNameET.getText().toString();
                gender = genderSP.getSelectedItem().toString();
                profileURL = profileUrlET.getText().toString();

                if(fName.equals("") || lName.equals("") || gender.equals("") || profileURL.equals("")) {
                    Toast.makeText(EditProfileActivity.this, "Please make sure each field is filled out.", Toast.LENGTH_SHORT).show();
                } else {
                    User userSave = new User(fName, lName, gender, profileURL, currentUser.getFriendsUID(), currentUser.getTripsID(), currentUser.getKey());
                    myRef = database.getReference("users").child(user.getUid());
                    myRef.setValue(userSave);

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(userSave.getfName() + " " + userSave.getlName())
                            .setPhotoUri(Uri.parse(userSave.getProfileURL()))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });

                    Toast.makeText(EditProfileActivity.this, "Profile data saved.", Toast.LENGTH_SHORT).show();

                }

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

    public User getUserProfile(String Uid) {

        final ArrayList<User> userList = new ArrayList<User>();


        myRef = database.getReference("users").child(Uid);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
                    Log.d("demo", snapshot.getValue(User.class).toString());
                    userList.add(snapshot.getValue(User.class));
                    Log.d(TAG, snapshot.getValue(User.class).getfName());
                    currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                    currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                    currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                    currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                    currentUser.setFriendsUID(snapshot.getValue(User.class).getFriendsUID());
                    currentUser.setTripsID(snapshot.getValue(User.class).getTripsID());
                    currentUser.setKey(snapshot.getValue(User.class).getKey());



                    setProfileFields(currentUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return currentUser;
    }

    public void setProfileFields(User user) {

        Log.d("PROFILE", user.getfName() + " | " + user.getlName());
        fNameET.setText(user.getfName());
        lNameET.setText(user.getlName());
        profileUrlET.setText(user.getProfileURL());

        Picasso.with(EditProfileActivity.this).load(user.getProfileURL()).into(profileUrlIV);

        Log.d("GENDER", user.getGender());
        for (int i=0;i<genderSP.getCount();i++){
            if (genderSP.getItemAtPosition(i).toString().equalsIgnoreCase(user.getGender())){
                genderSP.setSelection(i);
                break;
            }
        }

    }
}


