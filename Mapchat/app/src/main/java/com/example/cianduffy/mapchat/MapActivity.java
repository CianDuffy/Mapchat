package com.example.cianduffy.mapchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by David on 11/11/2016.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseReference database;
    GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        database = FirebaseDatabase.getInstance().getReference();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        // Check if we were successful in obtaining the map.
        if (map != null) {
            setUpMap();
        }
    }

    private void setUpMap() {
        final DatabaseReference ref = database.child("Locations").getRef();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    MessageLocation location = msgSnapshot.getValue(MessageLocation.class);
                    //System.out.println(post);
                    if (location != null) {
                        // App 2: Todo: Add a map marker here based on the loc downloaded
                        double lat = location.latitude;
                        double lon = location.longitude;
                        String locationText = "";
                        for(Message msg : location.messages) {
                            locationText += msg.messageText;
                        }
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(locationText));
                    }
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(53.283912, -9.063874));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        map.moveCamera(center);
        map.animateCamera(zoom);
    }
}
