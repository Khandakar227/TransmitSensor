package com.example.transmitsensor.controllers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.transmitsensor.libs.SensorDataListener;
import com.example.transmitsensor.models.SensorData;

;import java.util.ArrayList;
import java.util.List;

public class ManageSensor implements SensorEventListener {
    private Sensor rotationSensor;
    SensorStatus status;
    SensorData sensorData;
    SensorManager manager;
    static ManageSensor instance;
    private final List<SensorDataListener> listeners = new ArrayList<>();

    public void addSensorDataListener(SensorDataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeSensorDataListener(SensorDataListener listener) {
        listeners.remove(listener);
    }

    private ManageSensor(SensorManager manager) {
        if (manager == null)
            status = SensorStatus.NO_MANAGER;
        else {
            this.manager = manager;
            rotationSensor = this.manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if(rotationSensor == null) status = SensorStatus.NO_ROTATION_SENSOR;
            else {
                status = SensorStatus.OK;
                this.manager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

            }
        }
        sensorData = SensorData.getInstance();
    }
    public static ManageSensor getInstance() {
        return instance;
    }
    public static ManageSensor getInstance(SensorManager manager) {
        if(instance == null) {
            instance = new ManageSensor(manager);
        }
        return instance;
    }
    public SensorStatus getStatus() {
        return status;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            sensorData.setRV(event.values[0], event.values[1], event.values[2]);
            notifySensorDataChanged(event.sensor.getType(), sensorData.getRVvalues());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void unregisterListener() {
        manager.unregisterListener(this, rotationSensor);
    }
    public void registerListener() {
        if(manager == null || rotationSensor == null) return;
        manager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void notifySensorDataChanged(int sensorType, int[] sensorData) {
        for (SensorDataListener listener : listeners) {
            listener.onSensorDataChanged(sensorType, sensorData);
        }
    }
}
