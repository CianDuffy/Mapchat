package com.example.cianduffy.mapchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewMessageActivity extends AppCompatActivity {

    EditText newMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newMessageEditText = (EditText) findViewById(R.id.new_message_edit_text);
    }

    public void sendMessage(View view) {

        // Initialise Message Dictionary

        // Get Message Body
        String messageBody = newMessageEditText.getText().toString();
        System.out.println(messageBody);

        newMessageEditText.setText("");
        // Get Location

        // Create Timestamp

        // Add items to Message Dictionary

        // Upload to server

    }

    private void getLocation() {
        System.out.println("Getting GPS Coordinates");

    }
}
