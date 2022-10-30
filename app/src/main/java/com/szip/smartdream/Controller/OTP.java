package com.szip.smartdream.Controller;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.smartdream.Journal;
import com.szip.smartdream.R;

public class OTP extends AppCompatActivity {

    private TextView Submit;
    private String mobileNumber;
    private TextView phoneNumberTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        Submit = findViewById(R.id.submitBtn);
        initview();
    }

    private void initview(){


        Bundle extras = getIntent().getExtras();
        if (extras!=null){
        //    mobileNumber = extras.getString("mobileNumber");
        }
        //phoneNumberTv.setText(mobileNumber);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HeightWeight.class);
                startActivity(intent);
            }
        });
    }
}