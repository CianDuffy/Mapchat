package com.example.cianduffy.mapchat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewMessageActivity extends AppCompatActivity {

    EditText newMessageEditText;

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newMessageEditText = (EditText) findViewById(R.id.new_message_edit_text);

        setupLocationManager();
    }

    public void sendMessage(View view) {

        // Initialise Message Dictionary

        // Get Message Body
        String messageBody = newMessageEditText.getText().toString();
        System.out.println("Message body = " + messageBody);

        // Get latitude and longitude of last known location
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();

            System.out.println("Latitude = " + latitude);
            System.out.println("Longitude = " + longitude);
        } else {
            System.out.println("Location = null");
        }

        // Create Timestamp
        Long currentTimeMillis = System.currentTimeMillis();
        String timeStamp = currentTimeMillis.toString();
        System.out.println("TimeStamp = " + timeStamp);

        // Add items to Message Dictionary

        // Upload to server

        // Clear text field
        newMessageEditText.setText("");
    }

    private void setupLocationManager() {
        // Get a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lastKnownLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            locationProvider = locationManager.NETWORK_PROVIDER;

            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }

        lastKnownLocation = getLastBestLocation();
    }

    private Location getLastBestLocation() {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] requiredPermissions = new String[1];
            requiredPermissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            requestPermissions(requiredPermissions, 2);
        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
}
