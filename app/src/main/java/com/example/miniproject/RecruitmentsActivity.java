package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecruitmentsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private RecyclerView rcvUsers;
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<User> userArrayList;
    private SearchView searchView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitments);

        getSupportActionBar().setTitle("Recruitments");

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.recruitment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recruitment:
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
                    case R.id.jobsNearby:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        rcvUsers = findViewById(R.id.rcv_users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((this));
        rcvUsers.setLayoutManager(linearLayoutManager);

        userArrayList = new ArrayList<User>();
        userAdapter = new UserAdapter(userArrayList);
        userAdapter.addContext(RecruitmentsActivity.this);
        rcvUsers.setAdapter(userAdapter);


        EventChangeListener();

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvUsers.addItemDecoration(itemDecoration);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        LocationPickerActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    private void EventChangeListener() {
        fStore.collection("jobs")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            return;
                        }

                        for (DocumentChange dc: value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Map<String, Object> data = dc.getDocument().getData();
                                String title = (String) data.get("title");
                                String province = (String) data.get("province");
                                String icon = (String) data.get("icon");
                                String requirements = (String) data.get("requirements");
                                String description = (String) data.get("description");
                                String type = (String) data.get("type");
                                Long phone = (Long) data.get("phone");
                                Long minSalary = (Long) data.get("minSalary");
                                Long maxSalary = (Long) data.get("maxSalary");
                                if (icon.equals("restaurant")) {
                                    userArrayList.add(new User(R.drawable.restaurant_icon, title, "Province: "+ province,
                                            requirements, description, type,
                                            phone, minSalary, maxSalary));
                                } else if (icon.equals("cafe")) {
                                    userArrayList.add(new User(R.drawable.cafe_icon, title, "Province: "+ province,
                                            requirements, description, type,
                                            phone, minSalary, maxSalary));
                                } else {
                                    userArrayList.add(new User(R.drawable.tutor_icon, title, "Province: "+ province,
                                            requirements, description, type,
                                            phone, minSalary, maxSalary));
                                }
                            }
                        }

                        userAdapter.notifyDataSetChanged();
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}