package com.example.transmitsensor.models;

import com.example.transmitsensor.libs.GPSCoordinate;
import com.example.transmitsensor.libs.RotationVector;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorData {
    RotationVector rotationVector;
    GPSCoordinate gps;
    JSONObject json;
    private static SensorData instance;
    private SensorData() {
        rotationVector = new RotationVector();
        this.rotationVector.x = -1;
        this.rotationVector.y = -1;
        this.rotationVector.z = -1;

        this.gps = new GPSCoordinate();
        gps.latitude = 0;
        gps.longitude = 0;

        json = new JSONObject();
    }
    private int toDeg(float v) {
        int d = (int)Math.toDegrees(Math.asin(v)*2);
        return (d + 360) % 360;
    }
    public int[] getRVvalues() {
        return new int[]{rotationVector.x, rotationVector.y, rotationVector.z};
    }
    public static synchronized SensorData getInstance() {
        if (instance == null) {
            instance = new SensorData();
        }
        return instance;
    }

    public String getJSON() {
        try {
            JSONObject rotation = new JSONObject(), gpsData = new JSONObject();
            rotation.put("x", rotationVector.x);
            rotation.put("y", rotationVector.y);
            rotation.put("z", rotationVector.z);
            json.put("rotationVector", rotation);

            gpsData.put("longitude", gps.longitude);
            gpsData.put("latitude", gps.latitude);
            json.put("gps", gpsData);

            return json.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }
    public  double[] getGPS() {
        return new double[]{gps.longitude, gps.latitude};
    }
    public synchronized void setGPS(double lon, double lat) {
        gps.longitude = lon;
        gps.latitude = lat;
    }
    public synchronized void setRV(float x, float y, float z) {
        rotationVector.x = toDeg(x);
        rotationVector.y = toDeg(y);
        rotationVector.z = toDeg(z);
    }
}
