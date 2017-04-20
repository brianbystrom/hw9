package com.example.brianbystrom.hw09;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "DEMO2";
    private EditText fNameET, lNameET, emailET, passwordET, cPasswordET;
    private TextView welcomeTV;
    private Button signUpBTN;
    private String fName, lName, email, password, cPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeTV = (TextView) findViewById(R.id.welcomeTV);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    welcomeTV.setText("Welcome back " + user.getDisplayName());
                    myRef = database.getReference("users").child(user.getUid());

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (com.google.firebase.database.DataSnapshot s: snapshot.getChildren()) {
                                Log.d("demo", snapshot.getValue(User.class).toString());
                                Log.d(TAG, snapshot.getValue(User.class).getfName());
                                currentUser.setfName(snapshot.getValue(User.class).getfName().toString());
                                currentUser.setlName(snapshot.getValue(User.class).getlName().toString());
                                currentUser.setGender(snapshot.getValue(User.class).getGender().toString());
                                currentUser.setProfileURL(snapshot.getValue(User.class).getProfileURL().toString());
                                welcomeTV.setText("Welcome back " + user.getDisplayName());


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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profileMI:
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                return true;
            case R.id.friendsMI:
                startActivity(new Intent(HomeActivity.this, FriendsActivity.class));
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
