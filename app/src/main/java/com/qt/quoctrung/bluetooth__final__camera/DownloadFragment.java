package com.qt.quoctrung.bluetooth__final__camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DownloadFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // init Camera X
    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private PreviewView viewFinder;

    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    Button btnTakePhoto;

    public DownloadFragment() {
        // Required empty public constructor
    }

    public static DownloadFragment newInstance(String param1, String param2) {
        DownloadFragment fragment = new DownloadFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTakePhoto = view.findViewById(R.id.camera_capture_button);
        cameraExecutor = Executors.newSingleThreadExecutor();
        viewFinder = view.findViewById(R.id.viewFinder);
        //((MainActivity)getActivity()).startCamera();

        outputDirectory = getOutputDirectory();

        if (allPermissionsGranted()) {
//            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "take photo", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    public void updateData(String data) {
        Log.d("GGG", "value before if " + data);
        if (data.contains("1")) {
            Log.d("GGG", "value after if " + data);
        }
    }

    private File getOutputDirectory() {
        File mediaDir = getActivity().getExternalMediaDirs()[0];
        File file = new File(mediaDir, getString(R.string.app_name));
        if (mediaDir.exists()) {
            mediaDir.delete();
        }
        file.mkdirs();
        return mediaDir;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void startCamera() {
        ((MainActivity) getActivity()).startCamera(viewFinder);
//        imageCapture = new ImageCapture.Builder().build();
//        ((MainActivity) getActivity()).cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = ((MainActivity) getActivity()).cameraProviderFuture.get();
//                Preview preview = new Preview.Builder().build();
//                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
//                preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
//                cameraProvider.unbindAll();
//                cameraProvider.bindToLifecycle(getActivity(), cameraSelector, preview, imageCapture);
//
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("GGG", "Use case binding failed", e);
//            }
//        }, cameraExecutor);
    }

    public void takePhoto() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FILENAME_FORMAT, Locale.US);
        simpleDateFormat.format(System.currentTimeMillis());
        File photoFile = new File(outputDirectory, simpleDateFormat + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                new Handler().post(() -> Toast.makeText(getActivity(), "Image Saved successfully", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getActivity(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }
}