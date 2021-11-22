package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnJob;
    TextView tvHello;
    ImageView ivLogout;
    ImageView ivUpdate;
    ArrayList<String> provinceList;
    Spinner spinnerJob,spinnerProvince;

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
        spinnerJob = (Spinner) findViewById(R.id.jobSpinner);

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
        provinceList.add("Hồ Chí Minh");
        provinceList.add("Hà Nội");
        ArrayAdapter<String > adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                provinceList);

        // Layout for All ROWs of Spinner.  (Optional for ArrayAdapter).
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProvince.setAdapter(adapter1);

        ivLogout.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        });

        ivUpdate.setOnClickListener(view -> {
            startActivity(new Intent(this, UpdateActivity.class));
        });

        btnJob.setOnClickListener(view -> {
            startActivity(new Intent(this, MapsActivity.class));
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