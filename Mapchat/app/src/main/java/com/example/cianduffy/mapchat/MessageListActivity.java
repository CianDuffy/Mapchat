package com.example.cianduffy.mapchat;

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
    ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messageList = (ListView) findViewById(R.id.message_list);
        messages = new ArrayList<String>();

        database = FirebaseDatabase.getInstance().getReference();
        setupMessageList();
    }

    private void setupMessageList() {
        final DatabaseReference ref = database.child("Messages").getRef();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    Message message = msgSnapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message.timestamp + ": " + message.messageText);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, messages);
        messageList.setAdapter(adapter);

    }

}
