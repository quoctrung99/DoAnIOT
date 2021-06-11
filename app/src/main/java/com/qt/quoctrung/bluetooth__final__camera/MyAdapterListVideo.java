package com.qt.quoctrung.bluetooth__final__camera;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qt.quoctrung.bluetooth__final__camera.model.Video;
import com.qt.quoctrung.bluetooth__final__camera.utils.ImageUtil;

import java.util.List;

class MyAdapterListVideo extends RecyclerView.Adapter<MyAdapterListVideo.MyViewHolder> {
    private List<Video> mlistVideo;

    private Context context;
    public  String FOLDER_VIDEO = "/data/user/0/com.qt.quoctrung.bluetooth__final__camera/files/videos/";
    String name;
    private int clickPosition = -1;

    public MyAdapterListVideo(List<Video> listVideo) {
        this.mlistVideo = listVideo;
    }

    @NonNull
    @Override
    public MyAdapterListVideo.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_video, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterListVideo.MyViewHolder holder, int position) {
        ImageUtil.loadImage(context, mlistVideo.get(position).getUrl(), holder.imgListVideo);
        name  = mlistVideo.get(position).getName();
        String nameOriginal = name.replace(".mp4", "");
        holder.txtNameFile.setText(nameOriginal);

        holder.itemView.setOnClickListener(view -> {
            clickPosition = position;
            String name  = mlistVideo.get(position).getName();
//                    Intent intent = new Intent(view.getContext(), PlayVideoActivity.class);
//                    view.getContext().startActivity(intent);
//                    valIntent = FOLDER_VIDEO + name;
//                    intent.putExtra("video", valIntent);
//                    view.getContext().startActivity(intent);
            ((ListVideoActivity)view.getContext()).displayDialog(Gravity.BOTTOM);
            ((ListVideoActivity)view.getContext()).setNameFileSelected(FOLDER_VIDEO + name);

        });

    }

    @Override
    public int getItemCount() {
        return mlistVideo.size();
    }

    public void removeItem() {
        if (clickPosition<0) return;
        mlistVideo.remove(clickPosition);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgListVideo;
        TextView txtNameFile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgListVideo = itemView.findViewById(R.id.imgListVideo);
            txtNameFile  = itemView.findViewById(R.id.txtNameFile);

        }
    }

    public void addListData(List<Video> listVideo){
        mlistVideo.clear();
        mlistVideo.addAll(listVideo);
        notifyDataSetChanged();
    }
    public String updateFolder(){
        return (FOLDER_VIDEO + name);
    }
}
