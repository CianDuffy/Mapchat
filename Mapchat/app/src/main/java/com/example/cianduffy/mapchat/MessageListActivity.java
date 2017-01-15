package com.example.cianduffy.mapchat;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by David on 16/12/2016.
 */

public class MessageListActivity extends AppCompatActivity {

    private DatabaseReference database;
    private ListView messageList;
    ArrayList<String> locationMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setup View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messageList = (ListView) findViewById(R.id.message_list);
        locationMessages = new ArrayList<String>();

        // get reference to Firebase DB
        database = FirebaseDatabase.getInstance().getReference();
        setupMessageList();
    }

    private void setupMessageList() {
        final DatabaseReference ref = database.child("Locations").getRef();
        // Download all MessageLocation Objects from the DB and add them to the ListView
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    MessageLocation location = msgSnapshot.getValue(MessageLocation.class);
                    if (location != null) {
                        locationMessages.add(location.toString());
                    }
                }
                ref.removeEventListener(this);
                populateList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void populateList() {
        // Add messages to ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                                           android.R.layout.simple_list_item_1,
                                                           android.R.id.text1,
                                                           locationMessages);
        messageList.setAdapter(adapter);
    }

}
