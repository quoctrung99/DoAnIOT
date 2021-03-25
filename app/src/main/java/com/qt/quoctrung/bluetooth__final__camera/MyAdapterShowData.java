package com.qt.quoctrung.bluetooth__final__camera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MyAdapterShowData extends RecyclerView.Adapter<MyAdapterShowData.ShowData> {
    List<String> mData;

    public MyAdapterShowData(List<String> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyAdapterShowData.ShowData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShowData(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterShowData.ShowData holder, int position) {
        holder.txtData.setText(mData.get(position));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ShowData extends RecyclerView.ViewHolder {
        TextView txtData;
        public ShowData(@NonNull View itemView) {
            super(itemView);
            txtData = itemView.findViewById(R.id.txtValue);
        }
    }

    public void addText(String string, int position) {
        mData.add(string);
        notifyItemInserted(position);
    }


}
