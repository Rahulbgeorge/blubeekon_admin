package com.example.rahulstudy.blubeekon_admin;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeintent=  new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeintent);

            }
        },3000);


    }
}
