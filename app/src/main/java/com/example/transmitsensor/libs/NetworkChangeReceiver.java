package com.example.transmitsensor.libs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private final NetworkChangeListener networkChangeListener;

    public NetworkChangeReceiver(NetworkChangeListener listener) {
        this.networkChangeListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (networkChangeListener != null) {
                networkChangeListener.onNetworkChanged(isConnected, activeNetwork != null ? activeNetwork.getType() : 0);

            }
        }
    }
}
