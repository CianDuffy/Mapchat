package com.example.cianduffy.mapchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String hello;
    }

    public void openNewMessageActivity(View view) {
        System.out.print("WHATS UP!!!");
    }

    public void openMapActivity(View view) {

    }

    public void openMessageHistoryActivity(View view) {

    }
}
