package com.qt.quoctrung.bluetooth__final__camera;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothSocket bluetoothSocket;

    private static BluetoothManager bluetoothManager;

    public static BluetoothManager getInstance() {
        if (bluetoothManager == null) {
            bluetoothManager = new BluetoothManager();
        }
        return bluetoothManager;
    }

    public BluetoothManager() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    public boolean connectBluetooth(BluetoothDevice bluetoothDevice) {
        try {
            bluetoothSocket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);
            bluetoothSocket.connect();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(getUuid()));
                cancelDiscovery();
                bluetoothSocket.connect();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void cancelDiscovery() {
        bluetoothAdapter.cancelDiscovery();
    }

    public void startDiscovery() {
        bluetoothAdapter.startDiscovery();
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public Set<BluetoothDevice> listBluetoothDevice() {
        return bluetoothAdapter.getBondedDevices();
    }

    public boolean enable() {
        return bluetoothAdapter.enable();
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    private String getUuid(){
        Set<BluetoothDevice> pairedDevices = listBluetoothDevice();
        String s = "";
        for (BluetoothDevice device: pairedDevices){
            for (ParcelUuid uuid: device.getUuids()){
                s = uuid.toString();
            }
        }
        return s;
    }
}
