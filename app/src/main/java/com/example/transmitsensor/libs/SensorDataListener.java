package com.example.transmitsensor.libs;

public interface SensorDataListener {
    void onSensorDataChanged(int sensorType, int[] sensorData);
    void onSensorDataChanged(int typeAccessLocation, double[] sensorData);
}
