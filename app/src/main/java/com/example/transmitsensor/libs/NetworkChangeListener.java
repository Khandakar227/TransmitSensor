package com.example.transmitsensor.libs;


public interface NetworkChangeListener {
    void onNetworkChanged(boolean isConnected, int connectionType);
}
