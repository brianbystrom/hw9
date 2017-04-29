package com.example.brianbystrom.hw09;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "DEMO";
    private EditText fNameET, lNameET, emailET, passwordET, cPasswordET, genderET;;
    private Button signUpBTN;
    private String fName, lName, email, password, cPassword, gender;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRefAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        fNameET = (EditText) findViewById(R.id.fNameET);
        lNameET = (EditText) findViewById(R.id.lNameET);
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        cPasswordET = (EditText) findViewById(R.id.cPasswordET);
        genderET = (EditText) findViewById(R.id.genderEt);
        signUpBTN = (Button) findViewById(R.id.signUpBTN);

        database = FirebaseDatabase.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        signUpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName = fNameET.getText().toString();
                lName = lNameET.getText().toString();
                email = emailET.getText().toString();
                password = passwordET.getText().toString();
                cPassword = cPasswordET.getText().toString();
                gender = genderET.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    ArrayList<String> friends = new ArrayList<String>();
                                    friends.add(task.getResult().getUser().getUid());
                                    ArrayList<String> trips = new ArrayList<String>();
                                    trips.add("INIT");
                                    User user = new User(fName, lName, gender, "http://www.diaglobal.org/_Images/member/Generic_Image_Missing-Profile.jpg", friends, trips, task.getResult().getUser().getUid());

                                    String userID = mAuth.getCurrentUser().getUid();
                                    Log.d(TAG, userID);

                                    myRef = database.getReference("users").child(userID);
                                    myRefAllUsers = database.getReference("all");

                                    myRef.setValue(user);
                                    myRefAllUsers.child(fName.charAt(0)+"").setValue(task.getResult().getUser().getUid());

                                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));

                                    Toast.makeText(SignUpActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
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
}
