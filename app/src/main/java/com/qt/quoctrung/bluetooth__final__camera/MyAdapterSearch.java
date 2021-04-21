package com.qt.quoctrung.bluetooth__final__camera;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MyAdapterSearch extends RecyclerView.Adapter<MyAdapterSearch.SearchHolder> {
    List<BluetoothDevice> mListBlue;
    HomeFragment homeFragment;

    public MyAdapterSearch(List<BluetoothDevice> mListBlue) {
        this.mListBlue = mListBlue;
    }

    @NonNull
    @Override
    public MyAdapterSearch.SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterSearch.SearchHolder holder, int position) {
        holder.txtIDBlue.setText(mListBlue.get(position).getAddress());
        holder.txtNameBlue.setText(mListBlue.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mListBlue.size();
    }

    public class SearchHolder extends RecyclerView.ViewHolder {
        TextView txtNameBlue, txtIDBlue;
        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            txtIDBlue = itemView.findViewById(R.id.txtIDBlue);
            txtNameBlue = itemView.findViewById(R.id.txtNameBlue);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeFragment.getHomeFragment().clickItem(getAdapterPosition());
                }
            });
        }
    }

    public void addListData(List<BluetoothDevice> deviceList){
        mListBlue.clear();
        mListBlue.addAll(deviceList);
        notifyDataSetChanged();
    }
}
