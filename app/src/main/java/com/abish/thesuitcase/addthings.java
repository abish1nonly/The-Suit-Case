package com.abish.thesuitcase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class addthings extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Button addButton;
    private EditText itemName, itemDescription, itemPrice, emailAddress;
    private TextView clearText;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng selectedLocation;
    private static final int REQUEST_CODE = 101;
    private CheckBox locationCheckBox, purchaseChecked;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addthings, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getView().setVisibility(View.GONE);
        mapFragment.getMapAsync(this);
        db = FirebaseFirestore.getInstance();
        itemName = view.findViewById(R.id.item_name);
        itemDescription = view.findViewById(R.id.item_description);
        itemPrice = view.findViewById(R.id.price);

        addButton = view.findViewById(R.id.updateButton);
        addButton.setOnClickListener(this);
        clearText = view.findViewById(R.id.clear_Text);
        clearText.setOnClickListener(this);
        locationCheckBox = view.findViewById(R.id.locationCheckBox);
        purchaseChecked = view.findViewById(R.id.isChecked);
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        }

        locationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mapFragment.getView().setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == addButton) {
            String name = itemName.getText().toString().trim();
            String description = itemDescription.getText().toString().trim();
            String price = itemPrice.getText().toString().trim();
            boolean purchased = purchaseChecked.isChecked();

            if (locationCheckBox.isChecked()) {
                if(selectedLocation != null){
                    saveData(name, description, price, purchased);
                }else{
                    Toast.makeText(requireContext(), "Select a location", Toast.LENGTH_SHORT).show();
                }
            } else {
                saveData(name, description, price, purchased);
            }
        } else if (view == clearText) {
            // Handle clearText button click
        }
    }

    private void saveData(String name, String description, String price, Boolean purchased) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> thing = new HashMap<>();
        thing.put("Name", name);
        thing.put("Description", description);
        thing.put("Price", price);
        thing.put("timeAdded", new Timestamp(new Date()));
        thing.put("Purchased", purchased);
        if (selectedLocation == null) {
            // Save latitude and longitude to Firestore
            thing.put("Latitude", null);
            thing.put("Longitude", null);
        }else{
            thing.put("Latitude", selectedLocation.latitude);
            thing.put("Longitude", selectedLocation.longitude);
        }
        db.collection("SuitCase")
                .document(user.getEmail())
                .collection("Things")
                .document(name)
                .set(thing)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FSLog", "DocumentSnapshot added with ID: ");
                    Toast.makeText(requireContext(), "Item added!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("FSLog", "Error adding document", e);
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap != null){
            LatLng initialLocation = new LatLng(54.9, -1.38);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f));
            googleMap.addMarker(new MarkerOptions().position(initialLocation).title("Marker"));
            googleMap.setOnMapClickListener(latLng -> {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                selectedLocation = latLng;
            });
        }
    }
}
