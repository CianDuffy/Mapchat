package com.example.cianduffy.mapchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openNewMessageActivity(View view) {
        System.out.print("WHATS UP!!!");
        Intent intent = new Intent(this, NewMessageActivity.class);
        startActivity(intent);
    }

    public void openMapActivity(View view) {
//        openActivity(MapActivity.class);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void openMessageHistoryActivity(View view) {

    }

    private void openActivity(Class openingClass) {
        Intent intent = new Intent(this, openingClass);
        startActivity(intent);
    }
}
