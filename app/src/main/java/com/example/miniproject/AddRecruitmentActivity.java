package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddRecruitmentActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etPhone;
    Spinner jobSpinner;
    EditText etMinSalary;
    EditText etMaxSalary;
    EditText etRequirements;
    EditText etDescription;
    EditText etLatitude;
    EditText etLongitude;
    Spinner iconSpinner;
    Spinner provinceSpinner;
    ArrayList<String> provinceList;
    ArrayList<String> iconList;
    ArrayList<String> jobList;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Button btnAdd;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recruitment);
        getSupportActionBar().setTitle("Add Recruitment");

        etTitle = findViewById(R.id.etTitle);
        etPhone = findViewById(R.id.etPhone);
        jobSpinner = findViewById(R.id.jobSpinner);
        etMinSalary = findViewById(R.id.etMinSalary);
        etMaxSalary = findViewById(R.id.etMaxSalary);
        etRequirements = findViewById(R.id.etRequirements);
        etDescription = findViewById(R.id.etDescription);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        iconSpinner = findViewById(R.id.iconSpinner);
        provinceSpinner = findViewById(R.id.provinceSpinner);
        btnAdd = findViewById(R.id.btnAdd);

        Intent intent = getIntent();
        etLatitude.setText(intent.getStringExtra("lat"));
        etLongitude.setText(intent.getStringExtra("lng"));

        provinceList=new ArrayList<>();
        provinceList.add("Phú Yên");
        provinceList.add("TP. Hồ Chí Minh");
        provinceList.add("Hà Nội");
        ArrayAdapter<String > adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                provinceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(adapter);

        iconList=new ArrayList<>();
        iconList.add("tutor");
        iconList.add("restaurant");
        iconList.add("cafe");
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                iconList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iconSpinner.setAdapter(adapter);

        jobList=new ArrayList<>();
        jobList.add("Full time");
        jobList.add("Part time");
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                jobList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        String userID = user.getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                etPhone.setText(value.getString("phone"));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(AddRecruitmentActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                String title = etTitle.getText().toString();
                Long phone = Long.parseLong(etPhone.getText().toString());
                String type = jobSpinner.getSelectedItem().toString();
                Long minSalary = Long.parseLong(etMinSalary.getText().toString());
                Long maxSalary = Long.parseLong(etMaxSalary.getText().toString());
                String requirements = etRequirements.getText().toString();
                String description = etDescription.getText().toString();
                Double lat = Double.parseDouble(etLatitude.getText().toString());
                Double lng = Double.parseDouble(etLongitude.getText().toString());
                String icon = iconSpinner.getSelectedItem().toString();
                String province = provinceSpinner.getSelectedItem().toString();
                Map<String, Object> job = new HashMap<>();
                job.put("title", title);
                job.put("phone", phone);
                job.put("type", type);
                job.put("minSalary", minSalary);
                job.put("maxSalary", maxSalary);
                job.put("requirements", requirements);
                job.put("description", description);
                job.put("lat", lat);
                job.put("lng", lng);
                job.put("icon", icon);
                job.put("province", province);

                fStore.collection("jobs")
                        .add(job)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddRecruitmentActivity.this, "Add Recruitment Successful",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(),
                                        RecruitmentsActivity.class));
                                overridePendingTransition(0, 0);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddRecruitmentActivity.this, "Add Recruitment Failed",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


}