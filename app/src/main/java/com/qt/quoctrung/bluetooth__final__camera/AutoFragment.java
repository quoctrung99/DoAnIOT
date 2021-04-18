package com.qt.quoctrung.bluetooth__final__camera;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import hiennguyen.me.circleseekbar.CircleSeekBar;


public class AutoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private CircleSeekBar circleSeekBar;
    private TextView txtDegree;
    private EditText edtLengthAuto;
    private SeekBar seekBarLine;
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
        circleSeekBar = view.findViewById(R.id.circular);
        txtDegree = view.findViewById(R.id.txtDegree);
        seekBarLine = view.findViewById(R.id.seekBarLineAuto);
        edtLengthAuto = view.findViewById(R.id.edtLengthAuto);
        circleSeekBar.setSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangedListener() {
            @Override
            public void onPointsChanged(CircleSeekBar circleSeekBar, int points, boolean fromUser) {
                txtDegree.setText(points + "" + (char) 0x00B0);
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
                edtLengthAuto.setText(i + " cm");
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


}