package com.qt.quoctrung.bluetooth__final__camera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public BottomNavigationView bottomNav;
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
    String mData;
    // init navigation
    private static final int POS_HOME = 0;
    private static final int POS_DOWNLOAD = 1;
    private static final int POS_UPLOAD = 2;
    private static final int POS_MANUAL = 3;
    private static final int POS_AUTO = 4;
    public int posCheckedNav = POS_HOME;

    private boolean mIsBluetoothConnected = false;

    // init Fragment
    HomeFragment     homeFragment;
    DownloadFragment downloadFragment;
    UpLoadFragment   upLoadFragment;
    ManualFragment   manualFragment;
    AutoFragment     autoFragment;
    BluetoothDevice bluetoothDevice;


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
        switchFragment(R.id.fragment_container, new HomeFragment());
        setupNavigation(savedInstanceState);
    }

    private void setupNavigation(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getNameFragment());
        }

        setCheckedMenu(posCheckedNav);
        if (null == savedInstanceState) {
            Menu menu = bottomNav.getMenu();
            this.onNavigationItemSelected(menu.findItem(R.id.bottom_nav));
        }
        bottomNav.setOnNavigationItemSelectedListener(this);

    }

    private void setCheckedMenu(int i) {
        bottomNav.getMenu().getItem(i).setChecked(true);

    }

    private String getNameFragment() {
        Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragmentInFrame instanceof HomeFragment) {
            posCheckedNav = POS_HOME;
            return "Home";
        }
        else if(fragmentInFrame instanceof AutoFragment) {
            posCheckedNav = POS_DOWNLOAD;
            return "Download";

        }

        else if(fragmentInFrame instanceof  ManualFragment) {
            posCheckedNav = POS_UPLOAD;
            return "Upload";
        }

        else if(fragmentInFrame instanceof  ManualFragment) {
            posCheckedNav = POS_MANUAL;
            return "Manual";
        }
        else if(fragmentInFrame instanceof  AutoFragment) {
            posCheckedNav = POS_AUTO;
            return "Auto";
        }

        return "Home";
    }

    public void createCamera(PreviewView viewFinder) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
    }

    public void switchFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void switchFragment(int frame, Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void switchFragment2(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
    }

    public void createClassSearchDevice(){
        new SearchDevices().execute();
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
                    stopAutoCapture();
                    switchFragment(R.id.fragment_container, new HomeFragment());
                    break;
                case R.id.download:

                    switchFragment2(new DownloadFragment());
                    break;
                case R.id.upload:
                    stopAutoCapture();
                    switchFragment(R.id.fragment_container, new UpLoadFragment());
                    break;
                case R.id.manual:
                    stopAutoCapture();
                    switchFragment(R.id.fragment_container, new ManualFragment());
                    break;
                case R.id.auto:
                    stopAutoCapture();
                    switchFragment(R.id.fragment_container, new AutoFragment());
                    break;
            }
        item.setChecked(true);

        return true;
    }

    public void stopAutoCapture() {
        if (mReadThread == null) return;
        mReadThread.stop();
        mReadThread = null;
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

    public boolean connectBluetooth() {
        try {
            mBTSocket.connect();
            return true;
        } catch (IOException e) {
            Log.e("GGG", e.getMessage());
            try {
                mBTSocket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket",
                        new Class[]{int.class}).invoke(bluetoothDevice, 1);
                mBTSocket.connect();
                return true;
            } catch (IOException | NoSuchMethodException ioException) {
                ioException.printStackTrace();
                return false;
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
                return false;
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
                return false;
            }
        }
    }
    public void clickItemBluetooth(int position) {
        bluetoothDevice = deviceItemList.get(position);
        try {
            mBTSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(getUuid()));
            bluetoothAdapter.cancelDiscovery();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUuid(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        String s = "";
        for (BluetoothDevice device: pairedDevices){
            for (ParcelUuid uuid: device.getUuids()){
                s = uuid.toString();
            }
        }
        return s;
    }

    private class ReadInput implements Runnable {
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
                while (!Thread.currentThread().isInterrupted()) {
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

    public void connectThread() {
        if (mBTSocket == null) {
            Toast.makeText(this, "Cần phải connect trước", Toast.LENGTH_SHORT).show();
            return;
        }
        mReadThread = new ReadInput();
    }

    public void disconnectBluetooth() {
        new DisConnectBluetooth().execute();
    }

    public class DisConnectBluetooth extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning());
                mReadThread = null;
            }

            try {
                mBTSocket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Toast.makeText(MainActivity.this, "Disconnected to device", Toast.LENGTH_SHORT).show();
            mIsBluetoothConnected = !aBoolean;
        }
    }

}