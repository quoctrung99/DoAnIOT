package com.qt.quoctrung.bluetooth__final__camera;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public BottomNavigationView bottomNav;
    public static final int BT_ENABLE_REQUEST = 10;
    public List<BluetoothDevice> deviceItemList = new ArrayList<>();
    public boolean isFind = false;

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, "ACTION_DISCOVERY_STARTED: ", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "ACTION_DISCOVERY_FINISHED: ", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                Toast.makeText(context, "ACTION_CONNECTION_STATE_CHANGED: ", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE.equals(action)) {
                Toast.makeText(context, "ACTION_REQUEST_DISCOVERABLE: ", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                Toast.makeText(context, "ACTION_SCAN_MODE_CHANGED: ", Toast.LENGTH_SHORT).show();
            } else if ("android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                disconnectThread();
            }
        }
    };

    private ConnectThread connectThread = null;

    private BluetoothManager bluetoothManager;
    public boolean bCheckDisconnect = false;

    public void clickSearch() {
        if (!isFind) {
            if (!bluetoothManager.isEnabled()) {
                requestEnableBluetooth();
            } else {
                searchBluetoothDevice();
            }
            isFind = true;
        } else {
            stopFindBluetooth();
            isFind = false;

        }
    }

    private void requestEnableBluetooth(){
        Intent enable_myBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enable_myBlue, BT_ENABLE_REQUEST);
    }

    private void stopFindBluetooth() {
        unregisterReceiver(mReceiver);
        bluetoothManager.cancelDiscovery();
    }

    private void findBluetooth() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        registerReceiver(mReceiver, filter);
        bluetoothManager.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothManager = BluetoothManager.getInstance();
        bottomNav = findViewById(R.id.bottom_nav);
        addFragment(new HomeFragment());
        setupNavigation(savedInstanceState);
    }

    private void setupNavigation(Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            Menu menu = bottomNav.getMenu();
            this.onNavigationItemSelected(menu.findItem(R.id.bottom_nav));
        }
        bottomNav.setOnNavigationItemSelectedListener(this);

    }

    public void addFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item == null) return true;
        int id = item.getItemId();
            switch (id){
                case R.id.home:
                    addFragment(new HomeFragment());
                    break;
                case R.id.download:
                    replaceFragment(new DownloadFragment());
                    break;
                case R.id.upload:
                    addFragment(new UpLoadFragment());
                    break;
                case R.id.manual:
                    addFragment(new ManualFragment());
                    break;
                case R.id.auto:
                    addFragment(new AutoFragment());
                    break;
            }
        item.setChecked(true);
        return true;
    }

    public void searchBluetoothDevice(){
        new SearchDevices().execute();
    }

    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... voids) {
            return new ArrayList<>(bluetoothManager.listBluetoothDevice());
        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> devices) {
            super.onPostExecute(devices);
            if (devices.size() > 0) {
                deviceItemList = devices;
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (int i=0; i< fragments.size(); i++) {
                    if (fragments.get(i) instanceof HomeFragment) {
                        HomeFragment homeFragment = (HomeFragment) fragments.get(i);
                        homeFragment.myAdapterSearch.addListData(deviceItemList);
                    }
                }
            } else {
                findBluetooth();
                Toast.makeText(MainActivity.this, "No paired devices found, please pair your serial BT device and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BT_ENABLE_REQUEST) {
            if (bluetoothManager.enable()) {
                searchBluetoothDevice();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            finish();
        }
    }

    public BluetoothDevice getBluetoothDevice(int position) {
        return deviceItemList.get(position);
    }


    public void connectThread() {
        if (bluetoothManager.getBluetoothSocket() == null) {
            Toast.makeText(this, "Cần phải connect trước", Toast.LENGTH_SHORT).show();
            return;
        }
        connectThread = new ConnectThread();
    }

    private class ConnectThread implements Runnable {
        private final Thread t;

        public ConnectThread() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;
            String mData;
            try {
                inputStream = bluetoothManager.getBluetoothSocket().getInputStream();
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] buffer = new byte[2048];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++){ }
                        final String strInput = new String(buffer, 0, i);
                        mData = strInput;
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for(int j = 0; j < fragments.size(); j++){
                            if(fragments.get(j) instanceof DownloadFragment){
                                DownloadFragment downloadFragment = (DownloadFragment) fragments.get(j);
                                downloadFragment.updateData(mData);
                            }
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void stop() {
            try {
                t.interrupt();
            } catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void disconnectThread() {
        new DisconnectThreadInput().execute();
    }
    public class DisconnectThreadInput extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (connectThread != null) {
                connectThread.stop();
                while (connectThread.isRunning());
                connectThread = null;
            }
            try {
                bluetoothManager.getBluetoothSocket().close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            bCheckDisconnect = true;
            Toast.makeText(MainActivity.this, "Disconnected to device", Toast.LENGTH_SHORT).show();
            displayDialogAngle(Gravity.CENTER);
        }
    }

    public void displayDialogAngle(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_step_angle);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(Gravity.CENTER != gravity);
        dialog.show();
    }


    }