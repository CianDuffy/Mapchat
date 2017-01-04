package com.example.cianduffy.mapchat;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewMessageActivity extends AppCompatActivity  implements LocationListener {

    EditText newMessageEditText;

    private LocationManager locationManager;
    private Location lastKnownLocation;
    private String messageText;
    private DatabaseReference database;

    private ArrayList<MessageLocation> existingLocations;
    private int MIN_DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newMessageEditText = (EditText) findViewById(R.id.new_message_edit_text);

        database = FirebaseDatabase.getInstance().getReference();

        setupExistingLocations();
        setupLocationManager();
    }

    private void setupExistingLocations() {
        final DatabaseReference ref = database.child("Locations").getRef();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                existingLocations = new ArrayList<>();
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    MessageLocation message = msgSnapshot.getValue(MessageLocation.class);
                    existingLocations.add(message);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setupLocationManager() {
        // Get a reference to the system MessageLocation Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) {
            Log.e("GPS", "exception occured " + e.getMessage());
        }
        catch (Exception e) {
            Log.e("GPS", "exception occured " + e.getMessage());
        }
    }

    public void composeMessage(View view) {
        messageText = newMessageEditText.getText().toString();

        if (canSendMessage()) {
            MessageLocation[] inRangeLocations = locationsWithin10Meters();
            Message message = createMessage(messageText);
            if(inRangeLocations.length == 0) {
                MessageLocation location = createLocation(message);
                // Upload to server
                database.child("Locations").push().setValue(location);
            } else {
                // NOT SURE ABOUT THIS ID YET
                MessageLocation location = inRangeLocations[0];
                database.child("Locations").child("LocationId").push().setValue(message);
            }
            // Clear text field
            newMessageEditText.setText("");
        } else if (lastKnownLocation == null) {
            handleMessageSendError();
        }
    }

    private boolean canSendMessage() {
        return messageText.length() > 0 && lastKnownLocation != null;
    }

    private void handleMessageSendError() {
        if (messageText.length() <= 0) {
            Log.e("ERROR", "Location unknown");
        } else if (lastKnownLocation == null) {
            Log.e("ERROR", "Location unknown");
        }
        CharSequence text = "Unable to send message";
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private static double EARTH_RADIUS = 6373000;

    private MessageLocation[] locationsWithin10Meters() {
        ArrayList<MessageLocation> locations = new ArrayList<MessageLocation>();
        double lat1 = lastKnownLocation.getLatitude();
        double lon1 = lastKnownLocation.getLongitude();

        for(MessageLocation location : existingLocations) {
            double lat2 = location.latitude;
            double lon2 = location.longitude;

            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double x = Math.pow(Math.sin(dlat/2), 2);
            double a = Math.pow((x + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon/2)), 2);
            double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double distance = EARTH_RADIUS * c;
            if (distance < MIN_DISTANCE) {
                locations.add(location);
            }
        }
        return (MessageLocation[]) locations.toArray();
    }

    public Message createMessage(String messageText) {
        // Initialise Message Object
        Message message = new Message();

        // Add Message Text
        message.messageText = messageText;

        // Add Timestamp
        message.timestamp = System.currentTimeMillis();
        return message;
    }

    private MessageLocation createLocation(Message message) {
        double lat = lastKnownLocation.getLatitude();
        double lon = lastKnownLocation.getLongitude();
        Message[] messages = new Message[]{message};
        return new MessageLocation(messages, lat, lon);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}