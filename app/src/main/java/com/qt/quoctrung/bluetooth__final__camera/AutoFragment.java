package com.qt.quoctrung.bluetooth__final__camera;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import hiennguyen.me.circleseekbar.CircleSeekBar;


public class AutoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1"; 
    private static final String ARG_PARAM2 = "param2";

    final static String Release = "N";
    final static String UP    = "U";
    final static String DOWN  = "D";
    final static String ZERO = "Z";

    private String mParam1;
    private String mParam2;
    private CircleSeekBar circleSeekBar;
    private TextView txtDegree;
    private EditText edtVelocityAuto, edtRadiusAuto;
    private EditText edtVelocityLine, edtDirectionLine, edtLengthLine;
    private SeekBar seekBarLine;
    private Button btnStartCAuto, btnStopCAuto;
    private Button btnStartLAuto, btnStopLAuto;
    private String valVelAuto, valRadiusAuto, valAngleAuto ;
    private String valVelLine, valDirectionLine, valLengthLine;
    private BluetoothManager bluetoothManager;
    private FloatingActionButton floatingAuto;
    public AutoFragment() {
        // Required empty public constructor
    }
    


    public static AutoFragment newInstance(String param1, String param2) {
        AutoFragment fragment = new AutoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ((MainActivity)getActivity()).connectThread();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_auto, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bluetoothManager = BluetoothManager.getInstance();
        circleSeekBar = view.findViewById(R.id.circular);
        seekBarLine = view.findViewById(R.id.seekBarLineAuto);
        edtVelocityAuto = view.findViewById(R.id.edtVelocityAuto);
        edtRadiusAuto   = view.findViewById(R.id.edtRadiusAuto);
        txtDegree = view.findViewById(R.id.txtDegree);
        edtVelocityLine  = view.findViewById(R.id.edtVelocityLine);
        edtDirectionLine = view.findViewById(R.id.edtDirectionLine);
        edtLengthLine = view.findViewById(R.id.edtLengthAuto);

        btnStartCAuto   = view.findViewById(R.id.btnStartCAuto);
        btnStartLAuto   = view.findViewById(R.id.btnStartLineAuto);

        btnStopCAuto    = view.findViewById(R.id.btnStopCAuto);
        btnStopLAuto    = view.findViewById(R.id.btnStopLineAuto);

        floatingAuto    = view.findViewById(R.id.floatingAuto);

        floatingAuto.setOnClickListener(view13 -> {
            displayDialogAuto(Gravity.CENTER);
        });

        btnStartCAuto.setOnClickListener(view1 -> {
            valVelAuto    = edtVelocityAuto.getText().toString().trim();
            valRadiusAuto = edtRadiusAuto.getText().toString().trim();
            valAngleAuto  = txtDegree.getText().toString().trim();

            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write(("A" + valVelAuto + "v" + valRadiusAuto + "r" + valAngleAuto + "a"  + "#").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnStartLAuto.setOnClickListener(view12 -> {
            valVelLine = edtVelocityLine.getText().toString().trim();
            valDirectionLine = edtDirectionLine.getText().toString().trim();
            valLengthLine = edtLengthLine.getText().toString().trim();
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write(("S" + valVelLine + "v" + valLengthLine + "l" + valDirectionLine + "d"  + "#").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnStopCAuto.setOnClickListener(view14 -> {
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write((Release).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnStopLAuto.setOnClickListener(view15 -> {
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write((Release).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        
        circleSeekBar.setSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangedListener() {
            @Override
            public void onPointsChanged(CircleSeekBar circleSeekBar, int points, boolean fromUser) {
                txtDegree.setText(points + "");
            }

            @Override
            public void onStartTrackingTouch(CircleSeekBar circleSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(CircleSeekBar circleSeekBar) {

            }
        });
        seekBarLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                edtLengthLine.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
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

    void displayDialogAuto(int gravity){
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

        imgCancel = dialog.findViewById(R.id.imgCancel);
        imgCameraUp = dialog.findViewById(R.id.btnCameraUp);
        imgCameraDown = dialog.findViewById(R.id.btnCameraDown);

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

        imgCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }


}