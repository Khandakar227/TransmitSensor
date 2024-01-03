package com.example.transmitsensor.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.transmitsensor.libs.LocationDataListener;
import com.example.transmitsensor.libs.SensorDataListener;

public class ManageGPS {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LocationDataListener locationDataListener;
    LocationManager locationManager;
    static ManageGPS instance;

    ManageGPS (LocationManager manager, LocationDataListener locationDataListener) {
        this.locationDataListener = locationDataListener;
        this.locationManager = manager;
    }

    public static ManageGPS getInstance(LocationManager manager, LocationDataListener locationDataListener) {
        if(instance == null) {
            instance = new ManageGPS(manager, locationDataListener);
        }
        return instance;
    }

    public static ManageGPS getInstance() {
        return instance;
    }

    public void requestLocationPermission(Activity context) {

        int checkFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    LOCATION_PERMISSION_REQUEST_CODE
            );

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                loadPermissionPage(context);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                        context,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        } else startLocationUpdates(context);
    }
    private void loadPermissionPage(Activity context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent, 0);
    }

    public void startLocationUpdates(Context context) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates from both GPS and network providers
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationDataListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationDataListener);
        }
    }
    public void removeListener() {
        if (locationManager != null && locationDataListener != null) {
            locationManager.removeUpdates(locationDataListener);
        }
    }
    public void removeSensorDataListener(SensorDataListener sdl) {
        locationDataListener.removeSensorDataListener(sdl);
    }
}
