package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.miniproject.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView company_name, job_description, salary, address, contact;
    private Button close_popup, direction;

    private LatLng startLatLng, destLatLng;
    private Location lastLocation;
    MapDirectionHelper mapDirectionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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


        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);


        // Add a marker in Sydney and move the camera
        LatLng HN = new LatLng(15.565957, 108.482744);
        LatLng HN1 = new LatLng(15.579167, 108.477274);
        LatLng HN2 = new LatLng(15.479167, 108.577274);
        mMap.addMarker(new MarkerOptions().position(HN).title("Marker"));
        mMap.addMarker(new MarkerOptions().position(HN1).title("Marker"));
        mMap.addMarker(new MarkerOptions().position(HN2).title("Marker"));


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
        // gửi request đến gg và đợi phản hồi
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lastLocation = location; //giá trị location trả về lưu vào biến lastlocation
                        }
                    }
                });
        // lưu lastlocation làm tọa độ start để tính khoảng cách và vẽ đường đi
        startLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(startLatLng).title("It's me"));
        CameraPosition point = new CameraPosition.Builder()
                .target(startLatLng)
                .zoom(14)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(point));
        mapDirectionHelper = new MapDirectionHelper(mMap, this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapDirectionHelper.clearDirectionResult();
    }


    public Marker addMarkerOnMap(double lat, double lng, String Name) {
        LatLng position = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(Name)
                .draggable(false);
        Marker marker = mMap.addMarker(markerOptions);
        return marker;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        mapDirectionHelper.clearDirectionResult();
        openPopupWindow(marker);
        return false;
    }

    private void openPopupWindow(Marker marker) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View PopupView = getLayoutInflater().inflate(R.layout.popup, null);
        company_name = (TextView) PopupView.findViewById(R.id.company_name);
        job_description = (TextView) PopupView.findViewById(R.id.job_desription);
        salary = (TextView) PopupView.findViewById(R.id.salary);
        address = (TextView) PopupView.findViewById(R.id.address);
        contact = (TextView) PopupView.findViewById(R.id.contact);


        company_name.setText("company name");
        job_description.setText("job_description");
        salary.setText("salary");
        address.setText("address");
        contact.setText("contact");

        direction = (Button) PopupView.findViewById(R.id.direction);
        close_popup = (Button) PopupView.findViewById(R.id.close_popup);

        dialogBuilder.setView(PopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                destLatLng = marker.getPosition();
                mapDirectionHelper.startDirection(startLatLng, destLatLng);
            }
        });
    }

}