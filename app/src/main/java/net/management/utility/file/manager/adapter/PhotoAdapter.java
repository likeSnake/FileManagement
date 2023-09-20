package net.management.utility.file.manager.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.management.utility.file.manager.R;
import net.management.utility.file.manager.bean.ImageBean;
import net.management.utility.file.manager.util.MyUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private ArrayList<ImageBean> photoPaths;
    private int selectedPosition;
    private ItemClickListener listener;
    private int selects=0;
    private ArrayList<String> select_list = new ArrayList<>();
    private boolean b = false;

    public PhotoAdapter(Context context, ArrayList<ImageBean> photoPaths, ItemClickListener listener) {
        this.context = context;
        this.photoPaths = photoPaths;
        Collections.reverse(this.photoPaths);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        ImageBean photoBean = photoPaths.get(position);


        Glide.with(context)
                .load(photoBean.getPath())
                .into(holder.ivPhoto);
        //
        if (b){
            if (!photoBean.getSelect()){
                holder.select.setImageResource(R.mipmap.select_no);
            }else {
                holder.select.setImageResource(R.mipmap.select_yes);
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = true;
                selectedPosition = holder.getAdapterPosition();
                if (photoBean.getSelect()){
                    selects--;
                    select_list.remove(photoBean.getPath());
                    photoBean.setSelect(false);
                    holder.select.setImageResource(R.mipmap.select_no);
                }else {
                    selects++;
                    photoBean.setSelect(true);
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
        return photoPaths.size();
    }


    public ArrayList<ImageBean> getSelectItems(){

        return photoPaths;
    }

    public void flushedListByPosition (int position){
        selects=0;

        MyUtil.MyLog("移除"+position);
        photoPaths.remove(position);
        notifyItemRemoved(position);

    }

    public void setNoe(){

    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageView select;
        RelativeLayout layout;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            select = itemView.findViewById(R.id.select);
            layout = itemView.findViewById(R.id.layout);

        }
    }

    public interface ItemClickListener{
        void onClick();
        void Deselect();
    }
}