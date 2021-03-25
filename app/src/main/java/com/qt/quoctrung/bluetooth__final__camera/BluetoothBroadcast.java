package com.qt.quoctrung.bluetooth__final__camera;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("abcd".equals(action)) {

        }
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //Toast.makeText(context, "ACTION_FOUND: " + device.getName(), Toast.LENGTH_SHORT).show();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            Toast.makeText(context, "ACTION_DISCOVERY_STARTED: ", Toast.LENGTH_SHORT).show();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Toast.makeText(context, "ACTION_DISCOVERY_FINISHED: ", Toast.LENGTH_SHORT).show();
        } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){
            Toast.makeText(context, "ACTION_CONNECTION_STATE_CHANGED: ", Toast.LENGTH_SHORT).show();
        } else if (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE.equals(action)){
            Toast.makeText(context, "ACTION_REQUEST_DISCOVERABLE: ", Toast.LENGTH_SHORT).show();
        } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
            Toast.makeText(context, "ACTION_SCAN_MODE_CHANGED: ", Toast.LENGTH_SHORT).show();
        }
    }
}
