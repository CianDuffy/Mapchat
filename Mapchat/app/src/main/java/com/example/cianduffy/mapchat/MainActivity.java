package com.example.cianduffy.mapchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private boolean allPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allPermissionsGranted = checkForRequiredPermissions();
    }

    public void openNewMessageActivity(View view) {
        // If location and Internet permissions granted
        if (allPermissionsGranted) {
            openActivity(NewMessageActivity.class);
        }
    }

    public void openMapActivity(View view) {
        openActivity(MapActivity.class);
    }

    public void openMessageHistoryActivity(View view) {

    }

    private void openActivity(Class openingClass) {
        Intent intent = new Intent(this, openingClass);
        startActivity(intent);
    }

    private boolean checkForRequiredPermissions() {
        boolean coarseLocationDenied = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
        boolean fineLocationDenied = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
        boolean permissionsDenied;
        boolean allPermissionsGranted;

        permissionsDenied = (coarseLocationDenied || fineLocationDenied);

        if (permissionsDenied) {
            String[] requiredPermissions = new String[2];
            requiredPermissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            requiredPermissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;

            requestPermissions(requiredPermissions, 1);
            
            coarseLocationDenied = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
            fineLocationDenied = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
            permissionsDenied = (coarseLocationDenied || fineLocationDenied);
        }

        allPermissionsGranted = !permissionsDenied;

        return allPermissionsGranted;
    }
}
