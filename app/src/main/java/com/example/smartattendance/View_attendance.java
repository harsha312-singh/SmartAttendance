package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class View_attendance extends AppCompatActivity {

    TextView tv;
    ListView lv;

    List<String> myarray=new ArrayList<>();
    ArrayAdapter<String> myadapter;
    DatePickerDialog.OnDateSetListener setListener;
    DatabaseReference databaseReference2;
    String Roll,sem,batch,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        getSupportActionBar().setTitle("View Attendance");

        tv=findViewById(R.id.textView4);
        lv=(ListView) findViewById(R.id.listView);


        Roll = getIntent().getStringExtra("Roll");
        sem = getIntent().getStringExtra("sem");
        batch = getIntent().getStringExtra("batch");

        Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        View_attendance.this,android.R.style.Theme_Holo_Dialog_MinWidth,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String d=dayOfMonth+"/"+month+"/"+year;
                date=year+"-";
                if(month<10)
                    date=date+"0"+month;
                else
                    date=date+month;
                date=date+"-";
                if(dayOfMonth<10)
                    date=date+"0"+dayOfMonth;
                else
                    date=date+dayOfMonth;
                tv.setText(d);
                display_attendance_fron_db(date);
            }
        };


        //Toast.makeText(View_attendance.this,sem+" "+batch,Toast.LENGTH_LONG).show();

    }

    private void display_attendance_fron_db(String date) {
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("attendance").child(sem).child(batch).child(date);
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String sub=ds.getKey();
                    for(DataSnapshot ds2:ds.getChildren()){
                        if(Roll.equals(ds2.getKey().toString())){
                            myarray.add(sub);
                        }
                    }
                }
                display_list(myarray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void display_list(List<String> myarray) {
        Toast.makeText(View_attendance.this,myarray.size()+"",Toast.LENGTH_SHORT).show();
        myadapter = new ArrayAdapter<String>(View_attendance.this,android.R.layout.simple_list_item_1,myarray);
        lv.setAdapter(myadapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.example,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.item1) {
            Intent i = new Intent(View_attendance.this,MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
