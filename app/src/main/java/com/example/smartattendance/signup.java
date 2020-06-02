package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class signup extends AppCompatActivity {

    EditText name,rollno,pass1,pass2;
    Button btn;
    RadioButton radioButtonMale,radioButtonfemal;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseauth;
    Spinner myspinner,myspinner2;
    String semester="";
    String gender="";
    String batch="";
    String Name, Rollno, Pass1, Pass2;
    private static String cryptoPass = "sup3rdup3rc00l";
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Signup Form");

        name=findViewById(R.id.name);
        rollno=findViewById(R.id.rollno);
        pass1=findViewById(R.id.pass1);
        pass2=findViewById(R.id.pass2);
        btn=findViewById(R.id.btn);
        radioButtonfemal=findViewById(R.id.btnfemale);
        radioButtonMale=findViewById(R.id.btnmale);
        myspinner=(Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String>myadapter = new ArrayAdapter<String>(signup.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.semester));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(myadapter);

        myspinner2=(Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String>myadapter2 = new ArrayAdapter<String>(signup.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.batch));
        myadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner2.setAdapter(myadapter2);

        databaseReference = FirebaseDatabase.getInstance().getReference("student");
        firebaseauth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name=name.getText().toString();
                Rollno=rollno.getText().toString();
                Pass1 = pass1.getText().toString();
                Pass2= pass2.getText().toString();

                if(radioButtonMale.isChecked()){
                    gender="Male";
                }else if(radioButtonfemal.isChecked()){
                    gender="Female";
                }else{
                    Toast.makeText(signup.this,"Select gender",Toast.LENGTH_SHORT).show();
                    return;
                }

                semester=myspinner.getSelectedItem().toString().trim();
                batch=myspinner2.getSelectedItem().toString().trim();
                //Toast.makeText(signup.this,semester,Toast.LENGTH_SHORT).show();

                //Log.e("TAG",Name+" "+Rollno+" "+Pass1+" "+gender);
                if(Name.isEmpty() || Rollno.isEmpty() || Pass1.isEmpty() || Pass2.isEmpty() || gender.isEmpty())
                    Toast.makeText(signup.this,"Enter all the fields",Toast.LENGTH_SHORT).show();

                else if(!Pass1.equals(Pass2))
                    Toast.makeText(signup.this,"Passwords didn't match",Toast.LENGTH_SHORT).show();

                else {
                    String encrypted = Pass1;
                    try {
                        encrypted = encryptIt(Pass1);
                        Pass1 = encrypted;
                        Log.e("TAG",Pass1);
                    } catch (Exception e) {

                    }
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(Rollno)){
                                    Toast.makeText(signup.this,"Already Registered",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                student st = new student(Name, Rollno, Pass1, gender, semester,batch);
                                databaseReference.child(Rollno).setValue(st);
                                Toast.makeText(signup.this, "Registered", Toast.LENGTH_SHORT).show();
                                SharedPreferences prefers = getSharedPreferences("com.example.smartattendance_login_status", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefers.edit();
                                editor.putString("login_status", "on");
                                editor.apply();
                                SharedPreferences prefers2 = getSharedPreferences("com.example.smartattendance_roll_number", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = prefers2.edit();
                                editor2.putString("roll_number", Rollno);
                                editor2.apply();

                                Intent i = new Intent(signup.this, Fingerprint.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("Roll",Rollno);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    void login(View v){
        Intent i=new Intent(signup.this,Login_form.class);
        startActivity(i);
    }

    public static String encryptIt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(KEY.getBytes(),ALGORITHM);
        return key;
    }

}
