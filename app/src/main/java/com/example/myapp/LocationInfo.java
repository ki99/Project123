package com.example.myapp;

public class LocationInfo {
    private int index;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public LocationInfo(int index, String name, String address, double latitude, double longitude) {
        this.index = index;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setLongtitude(double longitude) {
        this.longitude = longitude;
    }
}