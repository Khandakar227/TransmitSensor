package com.example.transmitsensor.libs;

import android.util.Log;
import android.widget.Toast;

import com.example.transmitsensor.models.SensorData;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {
    SensorData sensorData;
    int port;

    public HttpServer(int port) {
        super(port);
        this.port = port;
        sensorData = SensorData.getInstance();
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            String data = sensorData.getJSON();
            return newFixedLengthResponse(Response.Status.OK, "application/json", data);
        } catch (Exception e) {
            Log.e("Server Error", "ERROR occured on the server", e);
            return  newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{}");
        }
    }

    public void restartServer() {
        this.stop();
        if (port > 0) {
            try {
                this.start();
            } catch (IOException e) {
                Utils.showToast("Error occurred when tried to start server", Toast.LENGTH_LONG);
            }
        }
    }

}
