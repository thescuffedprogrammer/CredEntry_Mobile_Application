package com.sp.iots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://iots-69dd8-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText emailLog = findViewById(R.id.emailLogin);
        final EditText passwordLog = findViewById(R.id.passLogin);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final TextView gotoReg = findViewById(R.id.registerPage);
        final TextView gotoReset = findViewById(R.id.resetPage);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailLogTxt = emailLog.getText().toString();
                final String passwordLogTxt = passwordLog.getText().toString();

                if (emailLogTxt.isEmpty() || passwordLogTxt.isEmpty()){
                    Toast.makeText(Login.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                }
                else {

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //check if email exists in firebase
                            if (snapshot.hasChild(emailLogTxt)){
                                //email exists in firebase, get password from firebase to match with login entered password
                                final String getPassword = snapshot.child(emailLogTxt).child("password").getValue(String.class);

                                if (getPassword.equals(passwordLogTxt)){
                                    Toast.makeText(Login.this, "SUCCESSFULLY LOGGED IN", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, SendOTP.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(Login.this, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }
        });

        gotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open register
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        gotoReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open reset
                startActivity(new Intent(Login.this, Reset.class));
            }
        });





    }
}