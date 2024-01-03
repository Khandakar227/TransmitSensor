package com.example.transmitsensor.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transmitsensor.R;
import com.example.transmitsensor.controllers.ManageGPS;
import com.example.transmitsensor.controllers.ManageSensor;
import com.example.transmitsensor.controllers.SensorStatus;
import com.example.transmitsensor.libs.HttpServer;
import com.example.transmitsensor.libs.LocationDataListener;
import com.example.transmitsensor.libs.NetworkChangeListener;
import com.example.transmitsensor.libs.NetworkChangeReceiver;
import com.example.transmitsensor.libs.SensorDataListener;
import com.example.transmitsensor.libs.Utils;
import com.example.transmitsensor.libs.services.SensorService;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends AppCompatActivity implements SensorDataListener, View.OnClickListener, NetworkChangeListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    TextView serverUrlText, rotationValueText, sensorStatusText, gpsText;
    EditText portText;
    Button changePortBtn;
    ManageSensor sensorsManager;
    NetworkChangeReceiver networkChangeReceiver;
    int PORT;

    HttpServer server;
    private ManageGPS manageGPS;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.toastInit(this);

        serverUrlText = findViewById(R.id.serverUrl);
        rotationValueText = findViewById(R.id.rotationValues);
        sensorStatusText = findViewById(R.id.sensorStatus);
        portText = findViewById(R.id.port);
        changePortBtn = findViewById(R.id.changePort);
        gpsText = findViewById(R.id.GPSValues);

        changePortBtn.setOnClickListener(this);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorsManager = ManageSensor.getInstance(sensorManager);

        if (sensorsManager.getStatus() == SensorStatus.OK) {
            sensorStatusText.setText("Sensor Status: Ok");
            sensorStatusText.setTextColor(Color.GREEN);
        }
        else if(sensorsManager.getStatus() == SensorStatus.NO_MANAGER) {
            sensorStatusText.setText("Status: Sensor manager is not available for your device");
            sensorStatusText.setTextColor(Color.RED);
        } else if (sensorsManager.getStatus() == SensorStatus.NO_ROTATION_SENSOR) {
            sensorStatusText.setText("Status: Rotation vector sensor is not available for your device");
            sensorStatusText.setTextColor(Color.RED);
        }
//      Work with location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationDataListener locationDataListener = new LocationDataListener();
        manageGPS = ManageGPS.getInstance(locationManager, locationDataListener);
        manageGPS.requestLocationPermission(this);
//      Add listener
        locationDataListener.addSensorDataListener(this);
        sensorsManager.addSensorDataListener(this);

        networkChangeReceiver = new NetworkChangeReceiver(this);
        registerNetworkChangeReceiver();

        PORT = 8081;
        server = new HttpServer(PORT);

        try {
            server.start();
            serverUrlText.setText("Server started running on " + Utils.getLocalIpAddress() + ":");
            portText.setText(String.format("%d", PORT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Start the sensor reading foreground service
        Intent serviceIntent = new Intent(this, SensorService.class);
        startService(serviceIntent);
    }


    void showToast(String message) {
        Toast.makeText((Context) this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorDataChanged(int sensorType, int[] sensorData) {
        if(sensorType == Sensor.TYPE_ROTATION_VECTOR) {
            String data = "x: "+ sensorData[0] + "\ny: "+ sensorData[1] + "\nz: " + sensorData[2];
            rotationValueText.setText(data);
        }
    }

    @Override
    public void onSensorDataChanged(int typeAccessLocation, double[] sensorData) {
        if (typeAccessLocation == LocationDataListener.TYPE_ACCESS_LOCATION) {
            String data = "long: "+ sensorData[0] + "\nlat: " + sensorData[1];
            gpsText.setText(data);
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.changePort) {
            String textValue = portText.getText().toString();
            int newPort = Integer.parseInt(textValue);
            if (PORT != newPort && newPort >= 1024 && newPort < 10000) {
                server.stop();
                server = new HttpServer(newPort);
                try {
                    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    PORT = newPort;
                    showToast("Server started running on port "+ PORT);
                } catch (IOException e) {
                    showToast("Error occurred. Please restart server. " + e.toString());
                }
            }
        }
    }
    private void registerNetworkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }
    private void unregisterNetworkChangeReceiver() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                manageGPS.startLocationUpdates(this);
            } else showToast("Location permission is not granted to access GPS");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, SensorService.class);
        stopService(serviceIntent);
        Utils.cancelNotification(this);

        sensorsManager.removeSensorDataListener(this);
        manageGPS.removeSensorDataListener(this);
        manageGPS.removeListener();
        sensorsManager.unregisterListener();
        unregisterNetworkChangeReceiver();
    }

    @Override
    public void onNetworkChanged(boolean isConnected, int connectionType) {
        if (!isConnected) {
            server.restartServer();
           serverUrlText.setText("Server started running on http://localhost:");
        } else {
            server.restartServer();
            serverUrlText.setText("Server started running on " + Utils.getLocalIpAddress()+":");
        }
    }
}