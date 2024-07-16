package com.abish.thesuitcase;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageThings extends Fragment implements
        ItemAdapter.OnEditButtonClickListener, ItemAdapter.OnDeleteButtonClickListener, ItemAdapter.OnShareButtonClickListener {

    private FirebaseFirestore db;
    private List<ItemModel> itemList;
    private ItemAdapter itemAdapter;
    private Button shareButton;
    private RecyclerView recyclerView;
    private View mapView;
    private boolean showMap = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_things2, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        mapView = view.findViewById(R.id.mapView);
        itemAdapter = new ItemAdapter(getContext(), itemList, this, this, this);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseFirestore.getInstance();
        loadItemsFromFirestore();

        return view;
    }

    @Override
    public void onEditButtonClick(ItemModel item) {
        String originalName = item.getName().replaceFirst("\\d+\\.\\s", "");
        Intent intent = new Intent(getActivity(), editThingsActivity.class);
        intent.putExtra("Name", originalName);
        intent.putExtra("Description", item.getDescription());
        intent.putExtra("Price", item.getPrice());
        intent.putExtra("latitude", item.getLatitude());
        intent.putExtra("longitude", item.getLongitude());
        startActivity(intent);
    }
    public void onDeleteButtonClick(ItemModel item, int position) {
        // Handle delete button click
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // User clicked "Yes" button
                    performDelete(item, position);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    public void onShareButtonClick(ItemModel item) {
        // Assuming you want to share the details of the first item in the list
        if (!itemList.isEmpty()) {
            ItemModel firstItem = itemList.get(0);
            String itemName = firstItem.getName();
            String itemDescription = firstItem.getDescription();
            String itemPrice = firstItem.getPrice();
            Double latitude = firstItem.getLatitude();
            Double longitude = firstItem.getLongitude();

            // Build the share message
            String shareMessage = "Check out this item:\n"
                    + "Name: " + itemName + "\n"
                    + "Description: " + itemDescription + "\n"
                    + "Price: " + itemPrice + "\n";

            if (latitude != null && longitude != null) {
                // Include the Google Maps location if available
                String locationUrl = "https://maps.google.com/?q=" + latitude + "," + longitude;
                shareMessage += "Location: " + locationUrl;
            }

            // Create an intent to share the message
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            // Start the activity to show the sharing options
            startActivity(Intent.createChooser(shareIntent, "Share Item Details"));
        } else {
            Toast.makeText(getContext(), "No items to share.", Toast.LENGTH_SHORT).show();
        }
    }
    private void performDelete(ItemModel item, int position) {
        String originalName = item.getName().replaceFirst("\\d+\\.\\s", "");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("SuitCase")
                    .document(user.getEmail())
                    .collection("Things")
                    .document(originalName)  // Assuming 'Name' is a unique identifier for the item
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Item deleted!", Toast.LENGTH_SHORT).show();
                        // Remove the item from the list and notify the adapter
                        itemList.remove(position);
                        itemAdapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error deleting item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
    private void loadItemsFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("SuitCase")
                    .document(user.getEmail())
                    .collection("Things")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            itemList.clear();
                            showMap = false;
                            int itemNumber = 1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Access the data using the correct field names
                                String name = document.getString("Name");
                                String description = document.getString("Description");
                                String price = document.getString("Price");
                                Double latitude = document.getDouble("Latitude");
                                Double longitude = document.getDouble("Longitude");
                                boolean purchaseChecked = document.getBoolean("Purchased");

                                // Create an ItemModel using the retrieved data
                                ItemModel item;
                                if (latitude != null && longitude != null && (latitude != 0 || longitude != 0)) {
                                    item = new ItemModel(name, description, price, latitude, longitude, purchaseChecked);
                                    showMap = true;
                                } else {
                                    // If latitude or longitude is null or (0,0), do not add to the list
                                    // and do not set showMap to true
                                    item = new ItemModel(name, description, price, null, null, purchaseChecked);
                                    mapView = null;
                                }
                                item.setName(itemNumber + ". " + name);
                                itemNumber++;
                                itemList.add(item);
                            }

                            itemAdapter.notifyDataSetChanged();
                            if (mapView != null) {
                                mapView.setVisibility(showMap ? View.VISIBLE : View.GONE);
                            }
                        } else {
                            // Handle error
                        }
                    });
        }
    }

}
