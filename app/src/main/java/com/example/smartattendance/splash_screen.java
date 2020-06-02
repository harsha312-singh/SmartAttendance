package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences= getSharedPreferences("com.example.smartattendance_login_status",
                        MODE_PRIVATE);
                String status = preferences.getString("login_status", "off");
                SharedPreferences preferences2= getSharedPreferences("com.example.smartattendance_roll_number",
                        MODE_PRIVATE);
                String status2 = preferences2.getString("roll_number", "null");

                if(status.equals("on"))
                {
                    Intent i=new Intent(splash_screen.this,Fingerprint.class);
                    i.putExtra("Roll",status2);
                    startActivity(i);
                }
                else
                {
                    startActivity(new Intent(splash_screen.this, Login_form.class));
                }
                finish();
            }
        }, 5000);
    }
}
