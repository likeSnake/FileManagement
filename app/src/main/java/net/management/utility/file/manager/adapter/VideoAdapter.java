package net.management.utility.file.manager.adapter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import net.management.utility.file.manager.R;
import net.management.utility.file.manager.bean.ImageBean;
import net.management.utility.file.manager.bean.VideoBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.PhotoViewHolder> {

    private PhotoAdapter.ItemClickListener listener;
    private int selects=0;
    private int selectedPosition;
    private Context context;
    private ArrayList<VideoBean> videoBeans;
    private ArrayList<String> select_list = new ArrayList<>();
    private boolean b = false;

    public VideoAdapter(Context context,ArrayList<VideoBean> videoBeans,PhotoAdapter.ItemClickListener listener) {
        this.context = context;
        this.videoBeans = videoBeans;
        this.listener = listener;

        Collections.reverse(this.videoBeans);
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        VideoBean videoBean = videoBeans.get(position);



        Glide.with(context)
                .asBitmap()
                .load(videoBean.getPath())
                .frame(TimeUnit.SECONDS.toMicros(1))
                .into(holder.ivPhoto);

        if (b){
            if (!videoBean.getSelect()){
                holder.select.setImageResource(R.mipmap.select_no);
            }else {
                holder.select.setImageResource(R.mipmap.select_yes);
            }
        }

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = true;
                selectedPosition = holder.getAdapterPosition();
                if (videoBean.getSelect()){
                    selects--;
                    select_list.remove(videoBean.getPath());
                    videoBean.setSelect(false);
                    holder.select.setImageResource(R.mipmap.select_no);
                }else {
                    selects++;
                    videoBean.setSelect(true);
                    holder.select.setImageResource(R.mipmap.select_yes);

                }
                if (selects!=0){
                    listener.onClick();
                }else {
                    listener.Deselect();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return videoBeans.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageView select;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            select = itemView.findViewById(R.id.select);

        }
    }
    public ArrayList<VideoBean> getSelectItems(){

        return videoBeans;
    }

    public void flushedListByPosition (int position){
        selects=0;
        videoBeans.remove(position);
        notifyItemRemoved(position);

    }
}