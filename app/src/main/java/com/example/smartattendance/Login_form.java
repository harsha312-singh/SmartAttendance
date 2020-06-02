package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class Login_form extends AppCompatActivity {

    EditText rollno,pass1;
    Button btn;
    DatabaseReference databaseReference;
    private static String cryptoPass = "sup3rdup3rc00l";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        getSupportActionBar().setTitle("Login Form");

        rollno = findViewById(R.id.roll);
        pass1 = findViewById(R.id.pas);
        btn = findViewById(R.id.btn2);

        databaseReference = FirebaseDatabase.getInstance().getReference("student");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Rollno = rollno.getText().toString();
                final String Pass1 = encryptIt(pass1.getText().toString());
                if(Rollno.isEmpty() || Pass1.isEmpty())
                    Toast.makeText(Login_form.this,"Enter all the fields",Toast.LENGTH_SHORT).show();


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int flag=0;
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            if(Rollno.equals(ds.child("rollno").getValue().toString()) && Pass1.equals(ds.child("password").getValue().toString())){
                                flag=1;
                                SharedPreferences prefers= getSharedPreferences("com.example.smartattendance_login_status",
                                        MODE_PRIVATE);
                                SharedPreferences.Editor editor= prefers.edit();
                                editor.putString("login_status", "on");
                                editor.apply();
                                SharedPreferences prefers2 = getSharedPreferences("com.example.smartattendance_roll_number", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = prefers2.edit();
                                editor2.putString("roll_number",Rollno);
                                editor2.apply();
                                Intent i=new Intent(Login_form.this,Fingerprint.class);
                                i.putExtra("Roll",Rollno);
                                startActivity(i);
                            }
                        }
                        if(flag==0)
                            Toast.makeText(Login_form.this,"Incorrect password",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    void register(View v){
        Intent i=new Intent(Login_form.this,signup.class);
        startActivity(i);
    }

    private String encryptIt(String value) {
        try {
            DESedeKeySpec keySpec = new DESedeKeySpec(cryptoPass.getBytes(StandardCharsets.UTF_8));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes(StandardCharsets.UTF_8);
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            //Log.d(TAG, "Encrypted: " + value + " -> " + encrypedValue);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }
}
