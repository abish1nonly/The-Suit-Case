package com.abish.thesuitcase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class editThingsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{
    private GoogleMap mMap;
    private Button updateButton, backButton;
    private EditText itemName, itemDescription, itemPrice, emailAddress;
    private TextView clearText;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng selectedLocation, initial;
    private static final int REQUEST_CODE = 101;
    private CheckBox editPurchase;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.editMapFragment);

        db = FirebaseFirestore.getInstance();
        itemName = findViewById(R.id.edit_item_name);
        itemDescription = findViewById(R.id.edit_item_description);
        itemPrice = findViewById(R.id.edit_price);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        String description = intent.getStringExtra("Description");
        String price = intent.getStringExtra("Price");
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);

        itemName.setText(name);
        itemDescription.setText(description);
        itemPrice.setText(price);

        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);

        editPurchase = findViewById(R.id.editPurchase);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
        editPurchase.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mapFragment.getView().setVisibility(View.VISIBLE);
            } else {
                mapFragment.getView().setVisibility(View.GONE);
            }
        });
        selectedLocation = new LatLng(latitude,longitude);
        initial = new LatLng(54.9, -1.38);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                updateMap();
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.updateButton) {
            updateDetailsToFirestore();
        } else if (v.getId() == R.id.backButton) {
            Intent intent = new Intent(editThingsActivity.this, ManageThings.class);
            startActivity(intent);
        }
    }

    private void updateDetailsToFirestore() {
        String name = itemName.getText().toString();
        String desc = itemDescription.getText().toString();
        String price = itemPrice.getText().toString();
        boolean purchased = editPurchase.isChecked();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> updatedThing = new HashMap<>();
        updatedThing.put("Name", name);
        updatedThing.put("Description", desc);
        updatedThing.put("Price", price);
        updatedThing.put("timeUpdated", new Timestamp(new Date()));
        updatedThing.put("Purchased", purchased);
        if (selectedLocation != null) {
            // Save latitude and longitude to Firestore
            updatedThing.put("Latitude", selectedLocation.latitude);
            updatedThing.put("Longitude", selectedLocation.longitude);
        }
        if (user != null) {
            String userEmail = user.getEmail();
            db.collection("SuitCase")
                    .document(userEmail)
                    .collection("Things")
                    .document(name)
                    .set(updatedThing)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void updateMap() {
        if (mMap != null) {

            if (selectedLocation.latitude != 0 || selectedLocation.longitude != 0) {
                // Add a marker only if the selected location is not (0,0)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f));
                mMap.addMarker(new MarkerOptions().position(selectedLocation).title("Selected Location"));

                mMap.setOnMapClickListener(latLng -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Updated Location"));
                    selectedLocation = latLng;
                });
            } else {
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 10f));
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }
}
