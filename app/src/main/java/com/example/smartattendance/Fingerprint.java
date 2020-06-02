package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Fingerprint extends AppCompatActivity {

    TextView Headerlevel;
    ImageView FingerprintImage;
    TextView paralevel;
    private FingerprintManager fpm;
    private KeyguardManager keyguardManager;
    static String Roll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        Headerlevel=(TextView) findViewById(R.id.Headerlevel);
        FingerprintImage=(ImageView)findViewById(R.id.FingerprintImage);
        paralevel=(TextView) findViewById(R.id.paralevel);
        Roll=getIntent().getStringExtra("Roll");


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            fpm=(FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager=(KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!fpm.isHardwareDetected()){

                paralevel.setText("There is no fingerprint scanner in your device");
            }
            else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!= getPackageManager().PERMISSION_GRANTED){
                paralevel.setText("The permission is not granted");
            }
            else if (!keyguardManager.isKeyguardSecure()){
                paralevel.setText("Please add lock to your device too keep the data secure");
            }
            else if (!fpm.hasEnrolledFingerprints()){
                paralevel.setText("Please add atleast one fingerprint to use this application");
            }
            else {
                paralevel.setText("Place your finger on the scanner to get the services");

                FingerprintHandler fingerprintHandler = new FingerprintHandler(Fingerprint.this);
                fingerprintHandler.startAuth(fpm, null);
            }
        }
    }
}
