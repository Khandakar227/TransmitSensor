package com.example.transmitsensor.libs;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.transmitsensor.models.SensorData;

import java.util.ArrayList;
import java.util.List;

public class LocationDataListener implements LocationListener {
    SensorData sensorData;
    public static final int TYPE_ACCESS_LOCATION = 123123;
    private final List<SensorDataListener> listeners = new ArrayList<>();

    public void addSensorDataListener(SensorDataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void notifySensorDataChanged(double[] sensorData) {
        for (SensorDataListener listener : listeners) {
            listener.onSensorDataChanged(TYPE_ACCESS_LOCATION, sensorData);
        }
    }

    public LocationDataListener() {
        sensorData = SensorData.getInstance();
    }

    public void removeSensorDataListener(SensorDataListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        sensorData.setGPS(longitude, latitude);
        notifySensorDataChanged(sensorData.getGPS());
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
        switch (status) {
            case LocationProvider.AVAILABLE:
                // GPS is available
                Utils.showToast("GPS is available", Toast.LENGTH_LONG);
                break;
            case LocationProvider.OUT_OF_SERVICE:
                // GPS is out of service
                Utils.showToast("GPS is out of service", Toast.LENGTH_LONG);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                // GPS is temporarily unavailable
                Utils.showToast("GPS is temporarily unavailable", Toast.LENGTH_LONG);
                break;
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        Utils.showToast("GPS is enabled", Toast.LENGTH_SHORT);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        Utils.showToast("GPS is disabled", Toast.LENGTH_LONG);
    }
}
