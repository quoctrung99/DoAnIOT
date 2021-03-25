package com.qt.quoctrung.bluetooth__final__camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar bottomNav;
    private String mParam1;
    private String mParam2;
    // Init bluetooth.
    public static final int BT_ENABLE_REQUEST = 10;
    public BluetoothAdapter bluetoothAdapter;
    public List<BluetoothDevice> deviceItemList = new ArrayList<>();
    public boolean isFind = false;
    public BluetoothBroadcast mReceiver = new BluetoothBroadcast();
    public BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    //private ReadInput mReadThread = null;
    String mData;


    public void clickSearch() {
        if (!isFind) {
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
            } else if (!bluetoothAdapter.isEnabled()) {
                requestEnableBluetooth();
            } else {
                new SearchDevices().execute();
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
        if (mReceiver.getDebugUnregister()) {
            unregisterReceiver(mReceiver);
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private void findBluetooth() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction("abcd");
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        registerReceiver(mReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    private ImageCapture imageCapture;

    public void startCamera(PreviewView viewFinder) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        imageCapture = new ImageCapture.Builder().build();
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("GGG", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bottomNav.setItemSelected(R.id.home, false);
        switchFragment(R.id.fragment_container, new HomeFragment());

        bottomNav.setOnItemSelectedListener(id -> {
            switch (id){
                case R.id.home:
                    switchFragment(R.id.fragment_container, new HomeFragment());
                    break;
                case R.id.download:
                    switchFragment(R.id.fragment_container, new DownloadFragment());
                    break;
                case R.id.upload:
                    switchFragment(R.id.fragment_container, new UpLoadFragment());
                    break;
            }

        });


    }

    public void createCamera(PreviewView viewFinder) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
    }

    public void switchFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void switchFragment(int frame, Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void createClassSearchDevice(){
        new SearchDevices().execute();
    }

    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... voids) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            return new ArrayList<>(pairedDevices);
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
            bluetoothAdapter.enable();
            new SearchDevices().execute();
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
    private class ReadInput implements Runnable {
        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }
        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[2048];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++){
                        }
                        final String strInput = new String(buffer, 0, i);
                        mData = strInput;
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for(int j = 0; j < fragments.size(); j++){
//                            if(fragments.get(j) instanceof UpLoadFragment){
//                                UpLoadFragment upLoadFragment = (UpLoadFragment) fragments.get(j);
//                                upLoadFragment.notifyData(mData);
//                            }
                            if(fragments.get(j) instanceof DownloadFragment){
                                DownloadFragment downloadFragment = (DownloadFragment) fragments.get(j);
                                downloadFragment.updateData(mData);
                            }
                        }
//                        if (fragments.size() >0 && fragments.get(0) instanceof ShowDataFragment) {
//                            ((ShowDataFragment) fragments.get(0)).notifyData(mData);
//                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (IOException e) {

            }catch (InterruptedException e){

            }
        }
        public void stop() {
            bStop = true;
        }
    }

    public void createReadInput() {
        mReadThread = new ReadInput();
    }

    // ------------------------//


}