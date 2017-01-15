package com.example.cianduffy.mapchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by David on 11/11/2016.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseReference database;
    GoogleMap map;
    private HashMap<Marker, String[]> markerMessages;
    private long mostRecentTimestamp = 0;
    private MessageLocation mostRecentLocation = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Setup view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        // Get reference to Firebase DB and setup Map if needed
        database = FirebaseDatabase.getInstance().getReference();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // If map is not already instantiated, initialise new MapFragment
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
        setupCustomMarkers();
        final DatabaseReference ref = database.child("Locations").getRef();
        // Pull Locations down from the Firebase DB
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                markerMessages = new HashMap<Marker, String[]>();
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    //  Add messages to the marker for each location
                    MessageLocation location = msgSnapshot.getValue(MessageLocation.class);
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        String[] formattedMessages = new String[location.getMessages().size()];
                        for(int i=0; i<formattedMessages.length; i++) {
                            Message msg = location.getMessages().get(i);
                            // Track location of most recently uploaded message
                            if (msg.getTimestamp() > mostRecentTimestamp) {
                                mostRecentLocation = location;
                                mostRecentTimestamp = msg.getTimestamp();
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.ENGLISH);
                            Date resultDate = new Date(msg.getTimestamp());
                            String dateString = simpleDateFormat.format(resultDate);

                            formattedMessages[i] = dateString + ": " + msg.getMessageText();
                        }
                        // Add marker with message to map
                        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
                        markerMessages.put(marker, formattedMessages);
                    }
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Zoom either to most recent location or default location
        double latitude = 53.283912;
        double longitude = -9.063874;

        if (mostRecentLocation != null) {
            latitude = mostRecentLocation.getLatitude();
            longitude = mostRecentLocation.getLongitude();
        }

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    private void setupCustomMarkers() {
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.marker_info, null);
                ListView list = (ListView) view.findViewById(R.id.marker_message_list);
                int index = findMarkerIndex(marker);
                String[] rows = (String[]) markerMessages.values().toArray()[index];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                                                        R.layout.marker_info_row,
                                                                        rows);
                list.setAdapter(adapter);
                return view;
            }
        });
    }

    private int findMarkerIndex(Marker marker) {
        int index = 0;
        for(Marker m : markerMessages.keySet()) {
            if (m.getPosition().equals(marker.getPosition())) {
                return index;
            }
            index++;
        }
        return 0;
    }
}
