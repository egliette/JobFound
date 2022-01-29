package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
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
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private Location lastLocation;
    private LatLng myLatLng, destlocation;
    private TextView tvInfo;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    FusedLocationProviderClient client;
    private ArrayList<Job> jobArrayList;
    private MapDirectionHelper mapDirectionHelper;
    LocationManager locationManager;
    Marker myMarker;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String mType;
    String mProvince;
    String mSalary;
    String mDistance;
    ProgressDialog progressDialog;

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
        mType = intent.getStringExtra("type");
        mProvince = intent.getStringExtra("province");
        mSalary = intent.getStringExtra("salary");
        mDistance = intent.getStringExtra("distance");
        tvInfo.setText(mType+"\n"+mProvince+"\n"+mSalary);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

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
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng).title("I'm here")
                                    .icon(getMarkerIcon("#008000"));
                            myLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            Long distance = Long.valueOf(MapsActivity.this.mDistance).longValue();
                            mMap.addCircle(new CircleOptions()
                                    .center(myLatLng)
                                    .radius(distance)
                                    .strokeColor(Color.GREEN));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            myMarker = googleMap.addMarker(options);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapDirectionHelper = new MapDirectionHelper(mMap,this);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();




        progressDialog.show();
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

                                Long salary = Long.valueOf(MapsActivity.this.mSalary).longValue();
                                Long distance = Long.valueOf(MapsActivity.this.mDistance).longValue();
                                Double true_distance = (Double) cal_distance(lat, lng,
                                        (Double) myLatLng.latitude, (Double) myLatLng.longitude);
                                if (MapsActivity.this.mProvince.equals(province) &&
                                        MapsActivity.this.mType.equals(type) &&
                                        salary<=maxSalary && salary>=minSalary &&
                                        true_distance < distance) {
                                    Job newJob = new Job(title, province, requirements,
                                            description, type, phone, minSalary,
                                            maxSalary, lat, lng);
                                    jobArrayList.add(newJob);
                                    LatLng latLng = new LatLng(lat, lng);
                                    MarkerOptions options = new MarkerOptions()
                                            .position(latLng)
                                            .title(title);
                                    mMap.addMarker(options);
                                }
                            }
                            progressDialog.dismiss();
                        }  else {
                            progressDialog.dismiss();
                        }
                    }
                });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                mapDirectionHelper.clearDirectionResult();
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
                        Button btnFindPath = (Button) dialog.findViewById(R.id.btnFindPath);
                        Button btnCall = (Button) dialog.findViewById(R.id.btnCall);


                        title.setText(job.getTitle());
                        province.setText(job.getProvince());
                        phone.setText(String.valueOf(job.getPhone()));
                        salary.setText(String.valueOf(job.getMinSalary())+"-"+
                                String.valueOf(job.getMaxSalary())+" VND/Month");
                        requirements.setText(job.getRequirements().replaceAll("newline", "\n"));
                        //Toast.makeText(MapsActivity.this, job.getRequirements().replaceAll("newline", "\n"), Toast.LENGTH_SHORT).show();
                        description.setText(job.getDescription());

                        btnFindPath.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // myLatLng giữ vị trí của mình hiện tại
                                // job.getLat() và job.getLng() là vĩ độ và kinh độ
                                // của công việc thứ i đang xét
                                destlocation = marker.getPosition();
                                mapDirectionHelper.startDirection(myLatLng, marker.getPosition());
                                dialog.dismiss();

                            }
                        });

                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone.getText().toString().trim()));
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                        });

                        dialog.show();

                    }
                }
                return false;
            }
        });
    }

    private BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        MarkerOptions options = new MarkerOptions()
                .position(latLng).title("I'm here")
                .icon(getMarkerIcon("#008000"));
        myLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (myMarker!=null) {
            myMarker.remove();
        }
        if (mMap!=null) {
           myMarker = mMap.addMarker(options);
        }

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private double cal_distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609344 * 1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}

