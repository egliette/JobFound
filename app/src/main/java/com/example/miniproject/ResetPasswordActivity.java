package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText etResetEmail;
    Button btnReset;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();

        etResetEmail = (EditText) findViewById(R.id.etResetEmail);
        btnReset =  (Button) findViewById(R.id.btnReset);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting...");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(view -> {
            resetPassword();
        });
    }

    private void resetPassword() {
        String email = etResetEmail.getText().toString();

        if (email.isEmpty()) {
            etResetEmail.setError("Email cannot be empty");
            etResetEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etResetEmail.setError("Please provide correct email syntax");
            etResetEmail.requestFocus();
            return;
        }

        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Reset Password Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}