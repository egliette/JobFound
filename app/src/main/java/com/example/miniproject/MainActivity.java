package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnJob;
    EditText etDistance;
    EditText etSalary;
    ArrayList<String> provinceList;
    Spinner spinnerJob,spinnerProvince;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        getSupportActionBar().setTitle("Search Jobs Nearby");
        //getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.jobsNearby);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.jobsNearby:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),
                                UpdateActivity.class));
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

//        ivLogout = (ImageView) findViewById(R.id.ivLogout);
//        ivUpdate = (ImageView) findViewById(R.id.ivUpdate);
//        tvHello = (TextView) findViewById(R.id.tvHello);
        btnJob = (Button) findViewById(R.id.btnJob);
        spinnerJob = (Spinner) findViewById(R.id.jobSpinner);
        etSalary = (EditText) findViewById(R.id.etSalary);
        etDistance = (EditText) findViewById(R.id.etDistance);

        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.job_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJob.setAdapter(adapter);
        spinnerJob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerProvince=(Spinner)findViewById(R.id.provinceSpinner);
        provinceList=new ArrayList<>();
        provinceList.add("Phú Yên");
        provinceList.add("TP. Hồ Chí Minh");
        provinceList.add("Hà Nội");
        ArrayAdapter<String > adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                provinceList);

        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProvince.setAdapter(adapter1);

//        ivLogout.setOnClickListener(view -> {
//            mAuth.signOut();
//            startActivity(new Intent(this, LoginActivity.class));
//        });
//
//        ivUpdate.setOnClickListener(view -> {
//            startActivity(new Intent(this, UpdateActivity.class));
//        });

        btnJob.setOnClickListener(view -> {
            getJobs();
        });

        if (mAuth.getCurrentUser() != null) {
            String userEmail = mAuth.getCurrentUser().getEmail();
//            tvHello.setText(tvHello.getText().toString()+userEmail);
        }
    }

    private void getJobs() {
        Intent  intent = new Intent(this, MapsActivity.class);
        String salary = etSalary.getText().toString().trim();
        String distance = etDistance.getText().toString().trim();
        if (salary.isEmpty()) {
            etSalary.setError("Please provide salary of job");
            return;
        }


        intent.putExtra("type", getJobtype());
        intent.putExtra("province", getProvince());
        intent.putExtra("salary", salary);
        intent.putExtra("distance", distance);

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        //nothing
    }

    public String getJobtype(){
        return spinnerJob.getSelectedItem().toString();
    }
    public String getProvince(){
        return spinnerProvince.getSelectedItem().toString();
    }
}