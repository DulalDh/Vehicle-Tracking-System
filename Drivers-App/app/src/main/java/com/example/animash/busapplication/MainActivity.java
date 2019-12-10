package com.example.animash.busapplication;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private String mEmail, mPassword;
    private Button mLogin;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = "driver1@sastc.com";
        mPassword = "1234567";

        if (isServiceOk()) {
            init();
        }
    }

    public boolean isServiceOk() {
        //  Log.d(TAG, "isServiceOk: checking google service version");
        int avilable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (avilable == ConnectionResult.SUCCESS) {
            //    Log.d(TAG, "isServiceOk: Google play services in working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avilable)) {
            //  Log.d(TAG, "isServiceOk: an error occured but we can fixed it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, avilable, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void init() {
        mLogin = (Button) findViewById(R.id.login);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Your Internet Connection Problem", Toast.LENGTH_SHORT).show();
                        } else {
                            //DatabaseReference DriverPost = mDatabase.push();
                            //  mDatabase.child("DriverActive").setValue("Driver is available");
                            mDatabase.child("DriverActive").child("Driver1").child("available").setValue("Driver is available");

                            String now = new SimpleDateFormat("hh:mm:aa").format(new java.util.Date().getTime());
                            mDatabase.child("DriverActive").child("Driver1").child("time").setValue("Start Time: " + now);
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
