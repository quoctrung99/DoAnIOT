package com.qt.quoctrung.bluetooth__final__camera;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qt.quoctrung.bluetooth__final__camera.model.Video;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.arthenica.mobileffmpeg.Config.getExternalLibraries;


public class DownloadFragment extends Fragment  {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextureView textureView;
    private FloatingActionButton floatingBtn;
    private PopupWindow window;
    private GridLayout gridLayout;
    private BluetoothManager bluetoothManager;

    String valRadiusCurve, valVideoLength, valTimeAction;
    String valOutput;
    public  String OUTPUT_VIDEO       = "/data/user/0/com.qt.quoctrung.bluetooth__final__camera/files/images/a.mp4";
    public  String INPUT_VIDEO        = "/data/user/0/com.qt.quoctrung.bluetooth__final__camera/files/images/IMG_%d.jpg";
    public  String FOLDER_IMAGE = "/data/user/0/com.qt.quoctrung.bluetooth__final__camera/files/images/";
    public  String FOLDER_VIDEO = "/data/user/0/com.qt.quoctrung.bluetooth__final__camera/files/videos/";


    //init params camera
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mState;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        return inflater.inflate(R.layout.fragment_download, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).connectThread();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textureView = view.findViewById(R.id.texture);
        floatingBtn = view.findViewById(R.id.floatingActionButton2);
        gridLayout  = view.findViewById(R.id.submitPopup);
        bluetoothManager = BluetoothManager.getInstance();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(receiver, new IntentFilter("updatePercent"));

        floatingBtn.setOnClickListener(view1 -> ShowPopupWindow());
        textureView.setSurfaceTextureListener(textureListener);

        startBackgroundThread();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                float percent = intent.getIntExtra("key", 0);
                TextView txtLoading = dialogAnimationMerge.findViewById(R.id.txtLoadingAnim);
                txtLoading.setText(percent+"%");
            }
        }
    };


    private void sendBroadcast(int value) {
        Intent intent = new Intent("updatePercent");
        intent.putExtra("key", value);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }


    private Dialog dialogAnimationMerge;
    private class MergeVidieo extends AsyncTask<Void, Void, Boolean> {
        private int percent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogAnimation(Gravity.CENTER);
            percentMerge();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return mergeImageToVideo(valOutput);
        }



        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                dialogAnimationMerge.dismiss();
                dialogConfirmMergeVideo(Gravity.CENTER);
                Log.d("FFFF", "onPostExecute: true");
            } else {
                Log.d("FFFF", "onPostExecute: false");
            }
        }

        private void dialogAnimation(int gravity){
            dialogAnimationMerge = dialogCustom(gravity, R.layout.dialog_animation_merge_video);
            dialogAnimationMerge.show();
        }

        private void percentMerge() {
            Config.enableStatisticsCallback(statistics -> {
                int progress = Integer.parseInt(String.valueOf(statistics.getTime())); // ms
                Log.d("GGGG",progress + " ");

                File file = new File(FOLDER_IMAGE);
                int totalImageSize = file.listFiles().length;
                percent =  (statistics.getVideoFrameNumber() *100)/totalImageSize ;
                sendBroadcast(percent);
                Log.d("GGGG","progressFinal" + percent + " " );
            });
        }
    }
    public void updateData(String data) {
        if (data.contains("1")) {
            takePicture();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity)getActivity()).connectThread();
        if (textureView.isAvailable()) {
            if (allPermissionsGranted()) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
//        ((MainActivity)getActivity()).disconnectBluetooth();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getActivity(), "stop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        stopBackgroundThread();
    }

    private String cameraId;
    private Size imageDimension;
    protected CameraDevice cameraDevice;
    protected CaptureRequest.Builder captureRequestBuilder;
    protected CameraCaptureSession cameraCaptureSessions;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private ImageReader imageReader;

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private void openCamera() {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.e("GGG", "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == cameraDevice) {
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getActivity(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e("GGG", "updatePreview error, return");
        }
     //   captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }



    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback(){

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            Log.d("GGGG","onCaptureStarted");
        //    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);

        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.d("GGGG","Focus lock successful");
        }
    };


    private int click = 0;
    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e("GGG", "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 1080;
            int height = 1920;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());

            //captureBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_AF_MODE_AUTO );
            captureBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);


            // Orientation
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            File folder = new File(getActivity().getFilesDir() + "/images");
            if (!folder.exists()) {
                boolean a = folder.mkdirs();
                Log.d("GGG", "takePicture: " + a);
            }
            final File file = new File(folder + "/IMG_" + click +".jpg");
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    try (Image image = reader.acquireLatestImage()) {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);

                    } finally {
                        click ++;
                        if (null != output) {
                            output.close();

                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(getActivity(), "Saved:" + file.getPath(), Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                    Log.d("GGG", "onCaptureCompleted: " + file.getPath());
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long folderSize(File file) {
        long length = 0;
        File[] folderFiles = file.listFiles();
        for (File f : folderFiles) {
            length += f.length();
        }

        return length;
    }

    private Boolean mergeImageToVideo (String nameFile) {
       // String cmd = "-start_number 0 -i "+ INPUT_VIDEO + " -r 25 -video_size 1280x720 -vcodec libx264 -pix_fmt yuv420p " + nameFile;
        String cmd = "-start_number 0 -i " + INPUT_VIDEO +
                " -r 25 -vcodec libx264 -crf 25 -vf scale=1920:1080 -pix_fmt yuv420p  " + nameFile;
       // String cmd = "-start_number 0 -i " + INPUT_VIDEO + " -c:v libx264 -t 60 -pix_fmt yuv420p -vf scale=1920:1080 " + OUTPUT_VIDEO;
        int rc = FFmpeg.execute(cmd);
        if (rc == RETURN_CODE_SUCCESS) {
            click = 0;
            Log.i(Config.TAG, "Command execution completed successfully.");
            return true;
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
            return false;
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            return false;
        }
    }

    public void dialogCreateVideoCustom(int gravity){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_settings);
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

        Button btnSendData, btnClose;
        btnSendData = dialog.findViewById(R.id.btnSendDataCamera);
        btnClose    = dialog.findViewById(R.id.btnCloseSettings);

        EditText edtRadiusCurve, edtVideoLength, edtTimeAction;
        edtRadiusCurve = dialog.findViewById(R.id.edtRadiusCurve);
        edtVideoLength = dialog.findViewById(R.id.edtVideoLength);
        edtTimeAction  = dialog.findViewById(R.id.edtTimeAction);

        btnSendData.setOnClickListener(view -> {
            valRadiusCurve = edtRadiusCurve.getText().toString().trim();
            valVideoLength = edtVideoLength.getText().toString().trim();
            valTimeAction  = edtTimeAction.getText().toString().trim();
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write(("C" + valRadiusCurve + "r" + valVideoLength + "l" + valTimeAction + "t" + "#").getBytes());
                Toast.makeText(getContext(), "Send data success!!!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });

        btnClose.setOnClickListener(view -> {
            window.getAttributes().windowAnimations = R.style.CloseDialogAnimation;
            dialog.dismiss();
        });

        dialog.show();
    }

    public void dialogMergeVideo(int gravity){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_merge_video);
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

        EditText edtFileName;
        Button btnConfirm, btnExitMerge;

        edtFileName  = dialog.findViewById(R.id.edtFileName);
        btnConfirm   = dialog.findViewById(R.id.btnConfirm);
        btnExitMerge = dialog.findViewById(R.id.btnExitMerge);

        btnConfirm.setOnClickListener(view -> {
            String valNameFile = edtFileName.getText().toString().trim() ;
            File file = new File(FOLDER_VIDEO);
            File[] listFile = file.listFiles();
            if(valNameFile.length() != 0){
                if(listFile.length == 0){
                    valOutput = FOLDER_VIDEO + valNameFile + ".mp4";
                    Log.d("GGG", valOutput);
                }
                else if(listFile.length != 0){
                    for(int i = 0; i < listFile.length; i++){
                        String name = listFile[i].getName();
                        String nameOriginal = name.replace(".mp4", "");
                        if(valNameFile.equals(nameOriginal)){
                            Toast.makeText(getContext(), "File name is exiting", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            valOutput = FOLDER_VIDEO + valNameFile + ".mp4";
                        }
                    }
                }
            }
            else{
                Toast.makeText(getContext(), "Please enter file name", Toast.LENGTH_SHORT).show();
                return;
            }
             if (!file.exists()) {
                 file.mkdirs();
             }

             new MergeVidieo().execute();
             dialog.dismiss();

        });

        btnExitMerge.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();

    }

    private Dialog dialogCustom(int gravity, int layout) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(Gravity.CENTER == gravity);
        return dialog;
    }

    private void dialogConfirmMergeVideo(int gravity){
        final Dialog dialog = dialogCustom(gravity, R.layout.dialog_confirm_merge);
        Button btnConfirmMerge;;
        btnConfirmMerge = dialog.findViewById(R.id.btnConfirmMerge);
        btnConfirmMerge.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void ShowPopupWindow(){
            CardView cardSettings, cardMergeVideo, cardPlayVideo, cardDeleteFiles;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_windown, null);
            window = new PopupWindow(layout, 670, 670, true);
            window.setAnimationStyle(R.style.DialogAnimation);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setOutsideTouchable(true);
            window.showAtLocation(layout, Gravity.CENTER, 25, 10);
            cardSettings    = layout.findViewById(R.id.settings);
            cardMergeVideo  = layout.findViewById(R.id.merge_video);
            cardPlayVideo   = layout.findViewById(R.id.play_video);
            cardDeleteFiles = layout.findViewById(R.id.delete_files);
            cardSettings.setOnClickListener(view -> {
                dialogCreateVideoCustom(Gravity.CENTER);
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
                window.dismiss();
            });

            cardMergeVideo.setOnClickListener(view -> {
                dialogMergeVideo(Gravity.CENTER);
                window.dismiss();
            });

            cardPlayVideo.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), ListVideoActivity.class);
                startActivity(intent);
                window.dismiss();

            });

            cardDeleteFiles.setOnClickListener(view -> {
                File folder = new File(getActivity().getFilesDir() + "/images");
                if (folder.exists()) {
                    try {
                        FileUtils.deleteDirectory(folder);
                        Toast.makeText(getActivity(),"Delete success", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"Delete fail", Toast.LENGTH_SHORT).show();
                    }
                }
                window.dismiss();

            });
    }


}