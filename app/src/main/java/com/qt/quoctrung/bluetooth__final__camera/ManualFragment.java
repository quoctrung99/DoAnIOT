package com.qt.quoctrung.bluetooth__final__camera;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
    final static String TOP = "T";    // TOP
    final static String BOTTOM = "B"; // BOTTOM
    final static String DATA = "D";
    final static String LEFT = "L";
    final static String RIGHT = "R";
    final static String MANUAL = "M";
    final static String Release = "N";    // TOP

    public ManualFragment() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        imgTop.setOnTouchListener((view1, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgTop.setImageResource(R.drawable.arrow_light);
                try {
                    ((MainActivity)getActivity()).mBTSocket.getOutputStream().write((TOP).getBytes());
                    Toast.makeText(getContext(), TOP, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                imgTop.setImageResource(R.drawable.arrow);
            }

            return true;
        });
        imgBottom.setOnTouchListener((view12, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgBottom.setImageResource(R.drawable.arrow_light);
                try {
                    ((MainActivity)getActivity()).mBTSocket.getOutputStream().write((BOTTOM).getBytes());
                    Toast.makeText(getContext(), BOTTOM, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imgBottom.setImageResource(R.drawable.arrow);
            }
            return true;
        });

        imgLeft.setOnTouchListener((view13, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                imgLeft.setImageResource(R.drawable.arrow_light);
                try {
                    ((MainActivity)getActivity()).mBTSocket.getOutputStream().write((LEFT).getBytes());
                    Toast.makeText(getContext(), LEFT, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imgLeft.setImageResource(R.drawable.arrow);
            }
            return true;

        });

        imgRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    imgRight.setImageResource(R.drawable.arrow_light);
                    try {
                        ((MainActivity)getActivity()).mBTSocket.getOutputStream().write((RIGHT).getBytes());
                        Toast.makeText(getContext(), RIGHT, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    imgRight.setImageResource(R.drawable.arrow);
                }
                return true;
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