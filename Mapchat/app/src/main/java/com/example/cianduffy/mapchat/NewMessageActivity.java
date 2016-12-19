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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewMessageActivity extends AppCompatActivity {

    EditText newMessageEditText;

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private String locationProvider;
    private DatabaseReference database;

    private ArrayList<Message> existingMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newMessageEditText = (EditText) findViewById(R.id.new_message_edit_text);

        database = FirebaseDatabase.getInstance().getReference();

        setupExistingMessages();
        setupLocationManager();
    }

    public void composeMessage(View view) {
        Message[] inRangeMessages = messagesWithin10Meters();

        String messageText = newMessageEditText.getText().toString();
        if (messageText.length() > 0) {
            if(inRangeMessages.length == 0) {
                sendMessage(messageText);
            } else {
                // append message
                for(Message message : inRangeMessages) {
                    database.child("Messages").child("id").child("messageText").setValue(message.messageText + messageText);
                }
            }
        }
    }

    private static double EARTH_RADIUS = 6373000;

    private Message[] messagesWithin10Meters() {
        ArrayList<Message> messages = new ArrayList<Message>();
        double lat1 = lastKnownLocation.getLatitude();
        double lon1 = lastKnownLocation.getLongitude();

        for(Message message : existingMessages) {
            double lat2 = message.latitude;
            double lon2 = message.longitude;

            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double x = Math.pow(Math.sin(dlat/2), 2);
            double a = Math.pow((x + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon/2)), 2);
            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double distance = EARTH_RADIUS * c;
            messages.add(message);
        }
        return (Message[]) messages.toArray();
    }

    public void sendMessage(String messageText) {

        if (lastKnownLocation != null) {

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

    private void setupExistingMessages() {
        final DatabaseReference ref = database.child("Messages").getRef();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                existingMessages = new ArrayList<Message>();
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    Message message = msgSnapshot.getValue(Message.class);
                    existingMessages.add(message);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}