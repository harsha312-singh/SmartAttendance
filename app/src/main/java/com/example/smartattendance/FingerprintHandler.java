package com.example.smartattendance;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static androidx.core.content.ContextCompat.startActivity;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;

    public FingerprintHandler(Context context) {

        this.context=context;

    }
    public  void  startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal= new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject,cancellationSignal,0,this,null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("There was an Authentication error. " + errString ,false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Authentication faled ",false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Scanning Error:" + helpString,false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app",true);
    }

    private void update(String s, boolean b) {
        TextView paralevel=(TextView) ((Activity)context).findViewById(R.id.paralevel);
        ImageView imageView=(ImageView) ((Activity)context).findViewById(R.id.FingerprintImage);
        paralevel.setText(s);
        if (!b){
            paralevel.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        }
        else {
            paralevel.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
            imageView.setImageResource(R.drawable.ic_done);
            Intent intent = new Intent(context,MainActivity.class);
            intent.putExtra("Roll",Fingerprint.Roll);
            context.startActivity(intent);
            Toast.makeText(context,"Authentication Successful",Toast.LENGTH_SHORT).show();
        }

    }
}
