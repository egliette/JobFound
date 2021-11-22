package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    
    Button btnJob;
    TextView tvHello;
    ImageView ivLogout;
    ImageView ivUpdate;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        ivLogout = (ImageView) findViewById(R.id.ivLogout);
        ivUpdate = (ImageView) findViewById(R.id.ivUpdate);
        btnJob = (Button) findViewById(R.id.btnJob);
        tvHello = (TextView) findViewById(R.id.tvHello);

        mAuth = FirebaseAuth.getInstance();

        ivLogout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        });

        ivUpdate.setOnClickListener(view -> {
            //startActivity(new Intent(this, UpdateActivity.class));
        });

        btnJob.setOnClickListener(view -> {
            //startActivity(new Intent(this, JobActivity.class));
        });

        if (mAuth.getCurrentUser() != null) {
            String userEmail = mAuth.getCurrentUser().getEmail();
            tvHello.setText(tvHello.getText().toString()+userEmail);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}