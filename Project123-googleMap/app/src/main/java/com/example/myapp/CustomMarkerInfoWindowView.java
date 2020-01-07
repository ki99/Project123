package com.example.myapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.zip.Inflater;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {
    private final View markerItemView;
    Context context;

    CustomMarkerInfoWindowView(Context context) {
        this.context = context;
        markerItemView = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null, false);
    }

    @Override
    public View getInfoWindow(Marker marker) { // 2
        LocationInfo loc = (LocationInfo) marker.getTag();  // 3

        TextView itemNameTextView = markerItemView.findViewById(R.id.loc_name);
        TextView itemAddressTextView = markerItemView.findViewById(R.id.address);
        Log.d("nametv", itemNameTextView+"");
        Log.d("addresstv", itemAddressTextView+"");
        Log.d("loc", loc+"");
        itemNameTextView.setText(loc.getIndex()+". "+loc.getName());
        itemAddressTextView.setText(loc.getAddress());
        return markerItemView;  // 4
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}