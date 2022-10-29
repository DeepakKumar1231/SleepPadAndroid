package com.szip.smartdream.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.szip.smartdream.R;

public class WatchVideo extends AppCompatActivity {

    private Button preorder,watchdemo,watchvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        preorder = findViewById(R.id.preOrdernowBtn);
        watchdemo = findViewById(R.id.watchVideoBtn);
        watchvider = findViewById(R.id.watchDemoBTn);
        initget();
    }


    private void initget(){
        preorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        watchdemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        watchvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}