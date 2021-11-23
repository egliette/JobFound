package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.miniproject.databinding.ActivityMapsBinding;
import com.example.miniproject.databinding.ActivityMaps2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private Location lastLocation;
    private LatLng myLatLng;
    private TextView tvInfo;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    FusedLocationProviderClient client;
    private ArrayList<Job> jobArrayList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tvInfo = (TextView) findViewById(R.id.tvInfo);
        jobArrayList = new ArrayList<Job>();

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String province = intent.getStringExtra("province");
        String salary = intent.getStringExtra("salary");
        tvInfo.setText(type+"\n"+province+"\n"+salary);


        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            myLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng).title("I'm here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        fStore.collection("jobs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Map<String, Object> data = document.getData();
                                String title = document.getString("title");
                                String province = document.getString("province");
                                String requirements = document.getString("requirements");
                                String description = document.getString("description");
                                String type = (String) data.get("type");
                                Long phone = (Long) data.get("phone");
                                Long minSalary = (Long) data.get("minSalary");
                                Long maxSalary = (Long) data.get("maxSalary");
                                Double lat = (Double) data.get("lat");
                                Double lng = (Double) data.get("lng");
                                Job newJob = new Job(title, province, requirements,
                                        description, type , phone, minSalary,
                                        maxSalary, lat, lng);
                                jobArrayList.add(newJob);
                                LatLng latLng = new LatLng(lat, lng);
                                MarkerOptions options = new MarkerOptions()
                                        .position(latLng)
                                        .title(title);


                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                                mMap.addMarker(options);

                            }
                        }  else {

                        }
                    }
                });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                for (int i = 0; i<jobArrayList.size(); i++) {
                    Job job = jobArrayList.get(i);
                    if (marker.getPosition().latitude == job.getLat() &&
                        marker.getPosition().longitude == job.getLng()) {
                        Dialog dialog = new Dialog(MapsActivity.this);
                        dialog.setContentView(R.layout.job_information);

                        TextView title = (TextView) dialog.findViewById(R.id.tvTitle);
                        TextView province = (TextView) dialog.findViewById(R.id.tvProvince);
                        TextView phone = (TextView) dialog.findViewById(R.id.tvPhone);
                        TextView salary = (TextView) dialog.findViewById(R.id.tvSalary);
                        TextView requirements = (TextView) dialog.findViewById(R.id.tvRequirements);
                        TextView description = (TextView) dialog.findViewById(R.id.tvDescription);

                        title.setText(job.getTitle());
                        province.setText(job.getProvince());
                        phone.setText(String.valueOf(job.getPhone()));
                        salary.setText(String.valueOf(job.getMinSalary())+"-"+
                                String.valueOf(job.getMaxSalary())+" VND/Month");
                        requirements.setText(job.getRequirements().replaceAll("newline", "\n"));
                        //Toast.makeText(MapsActivity.this, job.getRequirements().replaceAll("newline", "\n"), Toast.LENGTH_SHORT).show();
                        description.setText(job.getDescription());

                        dialog.show();

                    }
                }



                return false;
            }
        });
    }

}

