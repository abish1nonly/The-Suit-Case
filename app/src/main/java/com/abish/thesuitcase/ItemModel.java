package com.abish.thesuitcase;

import com.google.firebase.Timestamp;

public class ItemModel {
    private String ItemName;
    private String ItemDescription;
    private String ItemPrice;
    private Double Latitude;
    private Double Longitude;
    private boolean purchaseChecked;
    private Timestamp timeAdded; // Assuming you have a timestamp for when the item was added
    public ItemModel(){

    }
    public ItemModel(String name, String description, String price, Double latitude, Double longitude, boolean purchaseChecked) {
        this.ItemName = name;
        this.ItemDescription = description;
        this.ItemPrice = price;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.purchaseChecked = purchaseChecked;
    }

    public String getName() {
        return ItemName;
    }
    public void setName(String name) {
        this.ItemName = name;
    }

    public boolean isPurchaseChecked() { return purchaseChecked; }

    public String getDescription() {
        return ItemDescription;
    }

    public String getPrice() {
        return ItemPrice;
    }

    public double getLatitude() {
        return Latitude != null ? Latitude : 0.0; // Replace 0.0 with the default value you want to use

    }

    public double getLongitude() {
        return Longitude != null ? Longitude : 0.0; // Replace 0.0 with the default value you want to use

    }
}
