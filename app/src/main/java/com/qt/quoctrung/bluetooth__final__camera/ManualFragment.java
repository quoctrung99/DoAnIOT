package com.qt.quoctrung.bluetooth__final__camera;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;


public class ManualFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // init view
    ImageView imgTop, imgBottom, imgLeft, imgRight;
    final static String TOP = "T";
    final static String BOTTOM = "B";
   // final static String DATA = "D";
    final static String LEFT = "L";
    final static String RIGHT = "R";
    final static String MANUAL = "M";
    final static String Release = "N";

    final static String UP  = "U";
    final static String DOWN  = "D";

    final static String OPEN  = "O";
    final static String CLOSE  = "C";
    final static String ZERO = "Z";
    String valVelocity;
    String valRadius;
    private BluetoothManager bluetoothManager;
    private Button btnSendData;
    private EditText edtVelocity, edtRadius;
    private FloatingActionButton floatingManual;
    private ImageView btnCameraUp, btnCameraDown;

    public ManualFragment() {}

    public static ManualFragment newInstance(String param1, String param2) {
        ManualFragment fragment = new ManualFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        ((MainActivity)getActivity()).connectThread();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return inflater.inflate(R.layout.fragment_manual, container, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        final View view = getView();
        if (null != view) {
            view.getLayoutParams().height =  height;
            view.getLayoutParams().width = width;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgTop = view.findViewById(R.id.imgTop);
        imgBottom = view.findViewById(R.id.imgBottom);
        imgLeft   = view.findViewById(R.id.imgLeft);
        imgRight  = view.findViewById(R.id.imgRight);
        btnSendData = view.findViewById(R.id.btnSendData);
        edtVelocity = view.findViewById(R.id.edtVelocity);
        edtRadius   = view.findViewById(R.id.edtRadius);

        btnCameraUp = view.findViewById(R.id.btnCameraUp);
        btnCameraDown = view.findViewById(R.id.btnCameraDown);
        floatingManual = view.findViewById(R.id.floatingManual);


        floatingManual.setOnClickListener(view16 -> {
            displayDialogAngle(Gravity.CENTER);
        });
        bluetoothManager = BluetoothManager.getInstance();

        btnSendData.setOnClickListener(view15 -> {
            valVelocity = edtVelocity.getText().toString().trim();
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write(("M" + valVelocity + "v" + "#").getBytes());
                Toast.makeText(getContext(), "Sent data!!!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        imgTop.setOnTouchListener((view1, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgTop.setImageResource(R.drawable.arrow_light);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((TOP).getBytes());
                    Toast.makeText(getContext(), TOP, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                imgTop.setImageResource(R.drawable.arrow);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((Release).getBytes());
                    Toast.makeText(getContext(), Release, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        });

        imgBottom.setOnTouchListener((view12, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgBottom.setImageResource(R.drawable.arrow_light);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((BOTTOM).getBytes());
                    Toast.makeText(getContext(), BOTTOM, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imgBottom.setImageResource(R.drawable.arrow);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((Release).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return true;
        });

        imgLeft.setOnTouchListener((view13, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgLeft.setImageResource(R.drawable.arrow_light);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((LEFT).getBytes());
                    Toast.makeText(getContext(), LEFT, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imgLeft.setImageResource(R.drawable.arrow);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((Release).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;

        });

        imgRight.setOnTouchListener((view14, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgRight.setImageResource(R.drawable.arrow_light);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((RIGHT).getBytes());
                    Toast.makeText(getContext(), RIGHT, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                imgRight.setImageResource(R.drawable.arrow);
                try {
                    bluetoothManager.getBluetoothSocket().getOutputStream().write((Release).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        final View view = getView();
        if (null != view) {
            view.getLayoutParams().height =  height;
            view.getLayoutParams().width = width;
        }

    }

    private void displayDialogAngle(int gravity){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_step_angle);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
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

        ImageView imgCancel, imgCameraUp, imgCameraDown;
        Switch switchCamera;
        ImageView imgCamera;
        imgCancel = dialog.findViewById(R.id.imgCancel);
        imgCameraUp = dialog.findViewById(R.id.btnCameraUp);
        imgCameraDown = dialog.findViewById(R.id.btnCameraDown);
        switchCamera  = dialog.findViewById(R.id.switchCamera);
        imgCamera  = dialog.findViewById(R.id.imgCamera);

        imgCameraUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    imgCameraUp.setImageResource(R.drawable.arrow_light);
                    try {
                        bluetoothManager.getBluetoothSocket().getOutputStream().write((UP).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    imgCameraUp.setImageResource(R.drawable.arrow);
                    try {
                        bluetoothManager.getBluetoothSocket().getOutputStream().write((ZERO).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return true;
            }
        });

        imgCameraDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    imgCameraDown.setImageResource(R.drawable.arrow_light);
                    try {
                        bluetoothManager.getBluetoothSocket().getOutputStream().write((DOWN).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    imgCameraDown.setImageResource(R.drawable.arrow);
                    try {
                        bluetoothManager.getBluetoothSocket().getOutputStream().write((ZERO).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return true;
            }
        });

        switchCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getContext(), "ON", Toast.LENGTH_SHORT).show();
                    imgCamera.setImageResource(R.drawable.ic_dslr_camera_64_on);
                    try {
                        bluetoothManager.getBluetoothSocket().getOutputStream().write((OPEN).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    imgCamera.setImageResource(R.drawable.ic_dslr_camera_64);
                    try {
                        bluetoothManager.getBluetoothSocket().getOutputStream().write((CLOSE).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }


}