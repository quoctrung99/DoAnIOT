package com.qt.quoctrung.bluetooth__final__camera;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class HomeFragment extends Fragment {
    // Init View
    private RecyclerView mRecyclerView;
    private Button btnSearch;

    private List<BluetoothDevice> deviceItemList = new ArrayList<>();

    // init Adapter
    public MyAdapterSearch myAdapterSearch;
    MainActivity mainActivity;

    private static HomeFragment homeFragment;

    public static HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainActivity  = ((MainActivity)getActivity());
        btnSearch     = view.findViewById(R.id.btnSearch);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        // init Adapter
        myAdapterSearch = new MyAdapterSearch(deviceItemList);
        mRecyclerView.setAdapter(myAdapterSearch);
        btnSearch.setOnClickListener(view1 -> mainActivity.clickSearch());
    }

    @Override
    public void onResume() {
        super.onResume();
        homeFragment = this;
    }

    public void clickItem(int position){
        dialogCustom(Gravity.CENTER, position);
    }

    private String getUuid(){
        Set<BluetoothDevice> pairedDevices = mainActivity.bluetoothAdapter.getBondedDevices();
        String s = "";
        for (BluetoothDevice device: pairedDevices){
            for (ParcelUuid uuid: device.getUuids()){
                s = uuid.toString();
            }
        }
        //Log.d(TAG, "getUuid: " + s);
        return s;
    }

    private void dialogCustom(int gravity, int position){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_confirm);

        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if(Gravity.CENTER == gravity){
            dialog.setCancelable(false);

        }else{
            dialog.setCancelable(true);
        }
        Button btnConnect, btnDisConnect;
        btnConnect = dialog.findViewById(R.id.btnConnectDialog);
        btnDisConnect = dialog.findViewById(R.id.btnDisconnectDialog);

        btnConnect.setOnClickListener(view -> {
            mainActivity.clickItemBluetooth(position);
            if (mainActivity.connectBluetooth()) {
                mainActivity.switchFragment(new ManualFragment());
                mainActivity.bottomNav.setSelectedItemId(R.id.manual);
                Toast.makeText(mainActivity, "Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, "Connect fail", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        btnDisConnect.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

}