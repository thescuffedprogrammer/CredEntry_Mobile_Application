package com.sp.iots;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.PhoneAuthCredential;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Register extends AppCompatActivity {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    FirebaseAuth auth;

    //create obj of DBRef class to access firebase realtime DB
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://iots-69dd8-default-rtdb.firebaseio.com/");
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText name = findViewById(R.id.nameReg);
        final EditText emailReg = findViewById(R.id.emailReg);
        final EditText passwordReg = findViewById(R.id.passReg);
        final EditText conpassReg = findViewById(R.id.conpassReg);
        final Button regBtn = findViewById(R.id.regBtn);
        final TextView loginNow = findViewById(R.id.loginNow);
        final EditText phoneReg = findViewById(R.id.phoneReg);



        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get data from edittexts into string var
                final String nameTxt = name.getText().toString().trim();
                final String emailRegTxt = emailReg.getText().toString().trim();
                final String passRegTxt = passwordReg.getText().toString().trim();
                final String conpassRegTxt = conpassReg.getText().toString().trim();
                final String phoneRegText = phoneReg.getText().toString().trim();

                //check if user fills in all fields before sending data to firebase
                if (nameTxt.isEmpty() || emailRegTxt.isEmpty() || passRegTxt.isEmpty() || conpassRegTxt.isEmpty()){
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }


                //check if both pass match
                else if (!passRegTxt.equals(conpassRegTxt)) {
                    Toast.makeText(Register.this, "Passwords DO NOT match!", Toast.LENGTH_SHORT).show();
                }

                else {

                    databaseReference.child("users");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //check if email has been registered before
                            if (snapshot.hasChild(emailRegTxt)) {
                                Toast.makeText(Register.this, "Email has been registered before", Toast.LENGTH_SHORT).show();
                            } else {
                                //send data to firebase realtime DB, using email as unique identifier
                                databaseReference.child("users").child(emailRegTxt).child("name");
                                databaseReference.child("users").child(emailRegTxt).child("email").setValue(EncodeString(emailRegTxt));
                                databaseReference.child("users").child(emailRegTxt).child("password").setValue(EncodeString(passRegTxt));
                                databaseReference.child("users").child(emailRegTxt).child("phone").setValue(EncodeString(phoneRegText));


                                try {
                                    databaseReference.child(emailRegTxt).child("name").setValue(Register.encrypt(nameTxt));
                                    databaseReference.child(emailRegTxt).child("email").setValue(Register.encrypt(emailRegTxt));
                                    databaseReference.child(emailRegTxt).child("password").setValue(Register.encrypt(passRegTxt));
                                    databaseReference.child(emailRegTxt).child("phone").setValue(Register.encrypt(phoneRegText));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                //show successful msg then finish activity
                                Toast.makeText(Register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                Cleartxt();
                                finish();

                            }
                        }


                        private void Cleartxt(){
                            emailReg.setText("");
                            name.setText("");
                            passwordReg.setText("");
                            phoneReg.setText("");
                            conpassReg.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if(passwordReg.getText().toString().length()<8 &&!isValidPassword(passwordReg.getText().toString())){
                        Toast.makeText(Register.this, "Not Valid",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Register.this, "Valid",Toast.LENGTH_SHORT).show();
                    }



                    //send data to firebase realtime DB, using email as unique identifier


                    //show successful msg then finish activity
                    Toast.makeText(Register.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });




        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(String.valueOf(emailReg), actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }



    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    public void computeMD5Hash(String password) {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            //result.setText(MD5Hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Register.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Register.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(Register.KEY.getBytes(),Register.ALGORITHM);
        return key;
    }

}