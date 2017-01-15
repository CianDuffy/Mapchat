package com.example.cianduffy.mapchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Cian on 16/12/2016.
 */

public class MainActivity extends AppCompatActivity {

    private boolean allPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if required permissions granted
        allPermissionsGranted = checkForRequiredPermissions();
    }

    public void openNewMessageActivity(View view) {
        // Opens NewMessageActivity if location permissions granted
        if (allPermissionsGranted) {
            openActivity(NewMessageActivity.class);
        }
    }

    public void openMapActivity(View view) {
        // Opens the MessageListActivity
        openActivity(MapActivity.class);
    }

    public void openMessageHistoryActivity(View view) {
        // Opens the MessageListActivity
        openActivity(MessageListActivity.class);
    }

    private void openActivity(Class openingClass) {
        // Open the activity specified
        Intent intent = new Intent(this, openingClass);
        startActivity(intent);
    }

    private boolean checkForRequiredPermissions() {
        // Check if permissions have already been granted
        boolean coarseLocationDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
        boolean fineLocationDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
        boolean permissionsDenied;
        boolean allPermissionsGranted;

        // If not all permissions have been granted request them again
        permissionsDenied = (coarseLocationDenied || fineLocationDenied);
        if (permissionsDenied) {
            String[] requiredPermissions = new String[2];
            requiredPermissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            requiredPermissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;

            ActivityCompat.requestPermissions(this, requiredPermissions, 1);

            coarseLocationDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
            fineLocationDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
            permissionsDenied = (coarseLocationDenied || fineLocationDenied);
        }

        // return true only if all permissions are granted and false if any have been denied
        allPermissionsGranted = !permissionsDenied;
        return allPermissionsGranted;
    }
}
