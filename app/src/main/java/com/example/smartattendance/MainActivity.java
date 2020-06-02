package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {
    private ZXingScannerView scannerView;
    String Roll;
    String sem = "";
    String batch = "";
    String time = "";
    String subject = "";
    String name="";
    String dateTime;
    String stbatch, stsem;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Home");
        Roll = getIntent().getStringExtra("Roll");
        Toast.makeText(MainActivity.this, Roll, Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("student");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (Roll.equals(ds.getKey())) {
                        stsem = ds.child("semester").getValue().toString();
                        stbatch = ds.child("batch").getValue().toString();
                        name=ds.child("name").getValue().toString();
                        store_attendance_in_db(stbatch, stsem, Roll,name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void scancode(View view) {
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());


        setContentView(scannerView);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
        @Override
        public void handleResult(com.google.zxing.Result result) {
            String resultcode = result.getText();
            setContentView(R.layout.activity_main);
            scannerView.stopCamera();
            //Toast.makeText(MainActivity.this , resultcode , Toast.LENGTH_LONG).show();
            String s = resultcode.substring(0, 3);
            if (!s.equals("sub")) {
                Toast.makeText(MainActivity.this, "Not a valid QR", Toast.LENGTH_SHORT).show();
            } else {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                dateTime = simpleDateFormat.format(calendar.getTime());
                Pattern r = Pattern.compile("sub:(.*?):sub");
                Matcher m = r.matcher(resultcode);
                while (m.find()) {
                    subject = m.group(1);
                }
                Pattern r2 = Pattern.compile("batch:(.*?):batch");
                Matcher m2 = r2.matcher(resultcode);
                while (m2.find()) {
                    batch = m2.group(1);
                }

                Pattern r3 = Pattern.compile("time:(.*?):time");
                Matcher m3 = r3.matcher(resultcode);
                while (m3.find()) {
                    time = m3.group(1);
                }

                Pattern r4 = Pattern.compile("sem:(.*?):sem");
                Matcher m4 = r4.matcher(resultcode);
                while (m4.find()) {
                    sem = m4.group(1);
                }
            }
        }
    }

    boolean checktime(String time, String dateTime) {
        String hour = time.substring(5, 7);
        String min = time.substring(8, 10);
        String sec = time.substring(11);
        String hour2 = dateTime.substring(0, 2);
        String min2 = dateTime.substring(3, 5);
        String sec2 = dateTime.substring(6);
        int h1 = Integer.parseInt(hour);
        int h2 = Integer.parseInt(hour2) + 13;
        int m1 = Integer.parseInt(min);
        int m2 = Integer.parseInt(min2);
        int s1 = Integer.parseInt(sec);
        int s2 = Integer.parseInt(sec2);
        if (s2 - s1 >= 20 && m1 == m2 && h1 == h2)
            return true;
        if (s1 - s2 >= 40 && m2 - m1 == 1 && h1 == h2)
            return true;
        return false;
    }

    //2020-05-11
    private void store_attendance_in_db(String stbatch, String stsem, String roll,String name) {
        if (!sem.equals(stsem) || !stbatch.equals(batch))
            Toast.makeText(MainActivity.this, "Invalid student", Toast.LENGTH_SHORT).show();
        else if (!checktime(time, dateTime))
            Toast.makeText(MainActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
        else {
            String w1=time.substring(2,4);
            if(time.substring(3,4).equals(":"))
                w1="0"+time.substring(2,3);
            String date = "2020-" + "0"+ time.substring(0, 1) + "-" + w1;
            databaseReference2 = FirebaseDatabase.getInstance().getReference().child("attendance");
            databaseReference2.child(sem).child(batch).child(date).child(subject).child(Roll).setValue(name);
            Toast.makeText(MainActivity.this,"Attendence updated",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.example,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.item2) {
            Intent i = new Intent(MainActivity.this,View_attendance.class);
            i.putExtra("Roll",Roll);
            i.putExtra("sem",stsem);
            i.putExtra("batch",stbatch);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

