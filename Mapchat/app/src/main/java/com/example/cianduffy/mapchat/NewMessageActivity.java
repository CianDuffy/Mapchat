package com.example.cianduffy.mapchat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewMessageActivity extends AppCompatActivity {

    EditText newMessageEditText;

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private String locationProvider;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newMessageEditText = (EditText) findViewById(R.id.new_message_edit_text);

        database = FirebaseDatabase.getInstance().getReference();

        setupLocationManager();
    }

    public void sendMessage(View view) {
        String messageText = newMessageEditText.getText().toString();

        if (messageText.length() > 0 && lastKnownLocation != null) {

            // Initialise Message Object
            Message message = new Message();

            // Add Message Text
            message.messageText = messageText;

            // Add location information
            message.latitude = lastKnownLocation.getLatitude();
            message.longitude = lastKnownLocation.getLongitude();

            // Add Timestamp
            message.timestamp = System.currentTimeMillis();

            // Upload to server
            database.child("Messages").push().setValue(message);

            // Clear text field
            newMessageEditText.setText("");
        } else {
            Log.e("ERROR", "Location not found");
            Context context = getApplicationContext();
            CharSequence text = "Unable to send message";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
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
