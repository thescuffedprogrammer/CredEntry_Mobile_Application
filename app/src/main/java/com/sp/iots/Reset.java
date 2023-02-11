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
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Reset extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://iots-69dd8-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        final EditText emailRe = findViewById(R.id.emailReset);
        final EditText passRe = findViewById(R.id.passReset);
        final EditText conpassRe = findViewById(R.id.conpassReset);
        final Button resetPass = findViewById(R.id.resetBtn);
        final TextView backtoLogin = findViewById(R.id.loginPage);



        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String emailReTxt = emailRe.getText().toString().trim();
                final String passReTxt = passRe.getText().toString().trim();
                final String conpassReTxt = conpassRe.getText().toString().trim();



                if (emailReTxt.isEmpty() || passReTxt.isEmpty() || conpassReTxt.isEmpty()){
                    Toast.makeText(Reset.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }


                //check if both pass match
                else if (!passReTxt.equals(conpassReTxt)) {
                    Toast.makeText(Reset.this, "Passwords DO NOT match!", Toast.LENGTH_SHORT).show();
                }

                else {

                    databaseReference.child("users");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //check if email has been registered before
                            if (!snapshot.hasChild(emailReTxt)) {
                                Toast.makeText(Reset.this, "", Toast.LENGTH_SHORT).show();
                            } else {
                                //send data to firebase realtime DB, using email as unique identifier
                                databaseReference.child("users").child(emailReTxt).child("email").setValue(EncodeString(emailReTxt));
                                databaseReference.child("users").child(emailReTxt).child("password").setValue(EncodeString(passReTxt));
                                databaseReference.child("users").child(emailReTxt).child("conpass").setValue(EncodeString(conpassReTxt));

                                //show successful msg then finish activity
                                Toast.makeText(Reset.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        };



        backtoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Reset.this, Login.class));
            }
        });

            }
        });
    }


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

}


//    private void resetPass(String phReset, final String pReset){
//
//        final EditText passRe = findViewById(R.id.passReset);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Get auth credentials from the user for re-authentication
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phReset, pReset); // Current Login Credentials
//
//        // Prompt the user to re-provide their sign-in credentials
//        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                Log.d("value", "User re-authenticated.");
//
//                // Now change your email address \\
//                //----------------Code for Changing Email Address----------\\
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                user.updatePassword(passRe.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Reset.this, "Password Changed" + " Current Password is " + passRe.getText().toString(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//        });
//    }
