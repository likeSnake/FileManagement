package net.management.utility.file.manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.management.utility.file.manager.R;
import net.management.utility.file.manager.bean.VideoBean;
import net.management.utility.file.manager.bean.ZipBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ZipAdapter extends RecyclerView.Adapter<ZipAdapter.ZipViewHolder> {

    private PhotoAdapter.ItemClickListener listener;
    private int selects=0;
    private int selectedPosition;
    private Context context;
    private ArrayList<ZipBean> zipBeans;
    private ArrayList<String> select_list = new ArrayList<>();
    private boolean b = false;

    public ZipAdapter(Context context, ArrayList<ZipBean> zipBeans, PhotoAdapter.ItemClickListener listener) {
        this.context = context;
        this.zipBeans = zipBeans;
        this.listener = listener;

        Collections.reverse(this.zipBeans);
    }

    @NonNull
    @Override
    public ZipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_zip, parent, false);
        return new ZipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZipViewHolder holder, int position) {
        ZipBean zipBean = zipBeans.get(position);

        holder.zip_name.setText(zipBean.getName());
        holder.zip_size.setText(zipBean.getSize());
        holder.zip_time.setText(zipBean.getTime());


        if (b){
            if (!zipBean.getSelect()){
                holder.select.setImageResource(R.mipmap.select_no);
            }else {
                holder.select.setImageResource(R.mipmap.select_yes);
            }
        }

        holder.zip_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = true;
                selectedPosition = holder.getAdapterPosition();
                if (zipBean.getSelect()){
                    selects--;
                    select_list.remove(zipBean.getPath());
                    zipBean.setSelect(false);
                    holder.select.setImageResource(R.mipmap.select_no);
                }else {
                    selects++;
                    zipBean.setSelect(true);
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
        return zipBeans.size();
    }


    public class ZipViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout zip_layout;
        ImageView select;
        TextView zip_name,zip_size,zip_time;

        public ZipViewHolder(@NonNull View itemView) {
            super(itemView);
            zip_layout = itemView.findViewById(R.id.zip_layout);
            select = itemView.findViewById(R.id.select);
            zip_name = itemView.findViewById(R.id.zip_name);
            zip_size = itemView.findViewById(R.id.zip_size);
            zip_time = itemView.findViewById(R.id.zip_time);

        }
    }
    public ArrayList<ZipBean> getSelectItems(){

        return zipBeans;
    }

    public void flushedListByPosition (int position){
        selects=0;
        zipBeans.remove(position);
        notifyItemRemoved(position);

    }
}