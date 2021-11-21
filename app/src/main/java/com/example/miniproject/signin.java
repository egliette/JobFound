package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }
    public void forgotPassword(View view) {
        Intent intent=new Intent(this,forgotpassword.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        Intent intent=new Intent(this,signup.class);
        startActivity(intent);
    }
}