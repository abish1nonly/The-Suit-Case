package com.abish.thesuitcase;

import com.google.firebase.firestore.GeoPoint;

public class LocationData {
    private String itemName;
    private String itemDescription;
    private String itemPrice;
    private double latitude;
    private double longitude;

    // Constructors, getters, and setters

    public LocationData() {
        // Default constructor required for Firestore
    }

    public LocationData(String itemName, String itemDescription, String itemPrice, double latitude, double longitude) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}