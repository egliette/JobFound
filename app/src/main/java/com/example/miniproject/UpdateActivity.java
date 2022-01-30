package com.example.miniproject;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CODE = 10;

    TextInputEditText etName;
    TextInputEditText etAddress;
    TextInputEditText etBirthday;
    TextInputEditText etPhone;
    TextInputEditText etMail;
    Button btnUpdate;
    ProgressDialog progressDialog;
    ImageView ivAvatar;
    Uri mUri;

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent == null) {
                            return;
                        }
                        Uri uri = intent.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(getContentResolver(), uri);
                            setBitmapImageView(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public void setBitmapImageView(Bitmap bitmapImageView) {
        ivAvatar.setImageBitmap(bitmapImageView);
    }

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setTitle("Update Profile");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        return true;
                    case R.id.jobsNearby:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.logOut:
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.recruitment:
                        startActivity(new Intent(getApplicationContext(),
                                RecruitmentsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        etName = findViewById(R.id.etName);
        etAddress =  findViewById(R.id.etAddress);
        etBirthday =  findViewById(R.id.etBirthday);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etMail);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCanceledOnTouchOutside(false);
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        String userEmail = mAuth.getCurrentUser().getEmail();
        etMail.setText(userEmail);

        Calendar birthCalendar = Calendar.getInstance();
        final int birthYear = birthCalendar.get(Calendar.YEAR);
        final int birthMonth = birthCalendar.get(Calendar.MONTH);
        final int birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH);

        etBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            UpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            month = month + 1;
                            String date = day+"/"+month+"/"+year;
                            etBirthday.setText(date);
                        }
                    }, birthYear, birthMonth, birthDay);
                    datePickerDialog.show();
                }
            }
        });

        initUserProfile();

        btnUpdate.setOnClickListener(view -> {
            updateUserProfile();
        });

        ivAvatar.setOnClickListener(view -> {
            changeAvatar();
        });
    }

    private void changeAvatar() {
        onClickRequestPermission();
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
            return;
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            this.requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(UpdateActivity.this, "Need permission to do change avatar.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select picture"));
    }

    private void initUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();

        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.profile_update_user_icon).into(ivAvatar);
        //ivAvatar.setImageURI(photoUrl);

        String userID = user.getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                etName.setText(value.getString("name"));
                etAddress.setText(value.getString("address"));
                etBirthday.setText(value.getString("birthday"));
                etPhone.setText(value.getString("phone"));
            }
        });
    }

    private void updateUserProfile() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if(!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Please provide correct phone syntax");
            etPhone.requestFocus();
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(mUri)
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("address", address);
        user.put("birthday", birthday);
        user.put("phone", phone);

        String userID = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        progressDialog.show();
        documentReference.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateActivity.this, "Update profile successfully!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error adding document", e);
                        Toast.makeText(UpdateActivity.this, "Update profile error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }
}