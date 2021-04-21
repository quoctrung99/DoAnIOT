package com.qt.quoctrung.bluetooth__final__camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UpLoadFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Button btnControl, btnTurnOff;
    final static String ON  = "1";
    final static String OFF = "0";
    private MyAdapterShowData myAdapterShowData;
    RecyclerView recyclerViewFrag;
    private List<String> listData;
    private BluetoothManager bluetoothManager;

    public UpLoadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).connectThread();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return inflater.inflate(R.layout.fragment_up_load, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnControl = view.findViewById(R.id.btnControl);
        btnTurnOff = view.findViewById(R.id.btnTurnOff);
        recyclerViewFrag = view.findViewById(R.id.recyclerViewData);

        listData = new ArrayList<>();
        myAdapterShowData = new MyAdapterShowData(listData);
        recyclerViewFrag.setAdapter(myAdapterShowData);
        bluetoothManager = BluetoothManager.getInstance();
        btnControl.setOnClickListener(view12 -> {
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write((ON).getBytes());
                Toast.makeText(getContext(), "Turn on", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btnTurnOff.setOnClickListener(view1 -> {
            try {
                bluetoothManager.getBluetoothSocket().getOutputStream().write((OFF).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    int position = 0;
    public void notifyData(String data) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            if (myAdapterShowData != null) {
                myAdapterShowData.addText(data, position);
                scrollToBottom(recyclerViewFrag);
                position++;
            }
        });
    }

    private void scrollToBottom(final RecyclerView recyclerView) {
        // scroll to last item to get the view of last item
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int lastItemPosition = adapter.getItemCount() - 1;

        layoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
        recyclerView.post(() -> {
            // then scroll to specific offset
            View target = layoutManager.findViewByPosition(lastItemPosition);
            if (target != null) {
                int offset = recyclerView.getMeasuredHeight() - target.getMeasuredHeight();
                layoutManager.scrollToPositionWithOffset(lastItemPosition, offset);
            }
        });
    }
}