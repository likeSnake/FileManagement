package net.management.utility.file.manager.ui;

import static net.management.utility.file.manager.util.FileUtil.deleteSingleFile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.wang.avi.AVLoadingIndicatorView;

import net.management.utility.file.manager.App;
import net.management.utility.file.manager.R;
import net.management.utility.file.manager.ad.OpenAd;
import net.management.utility.file.manager.adapter.PhotoAdapter;
import net.management.utility.file.manager.adapter.VideoAdapter;
import net.management.utility.file.manager.bean.ImageBean;
import net.management.utility.file.manager.bean.VideoBean;

import java.util.ArrayList;

public class VideoAct extends AppCompatActivity{
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private ArrayList<VideoBean> videoBeans;
    private Button bt_delete_no,bt_delete_yes;
    private ImageView ic_back;
    private Dialog dialog;
    private FrameLayout ad_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video);
      //  initLoading();
        initUI();
        initData();
        initListener();
    }
    public void initData(){
        String videoGson = MMKV.defaultMMKV().decodeString("videoGson");

        videoBeans = new Gson().fromJson(videoGson,new TypeToken<ArrayList<VideoBean>>(){}.getType());

        start(videoBeans);
    }

    public void initUI(){

        recyclerView = findViewById(R.id.recyclerView);
        bt_delete_no = findViewById(R.id.bt_delete_no);
        bt_delete_yes = findViewById(R.id.bt_delete_yes);
        ic_back = findViewById(R.id.ic_back);
        ad_layout = findViewById(R.id.ad_layout);
    }
    public void initListener(){
        bt_delete_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                startDelete();
                /*new OpenAd(VideoAct.this, ad_layout, new App.OnShowAdCompleteListener() {
                    @Override
                    public void onShowAdComplete() {

                    }

                    @Override
                    public void TurnoffAds() {

                        startDelete();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailedToLoad() {
                        startDelete();
                        dialog.dismiss();
                    }

                    @Override
                    public void onAdFailedToShow() {
                        startDelete();
                        dialog.dismiss();
                    }

                    @Override
                    public void onAdLoaded() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClicked() {

                    }

                    @Override
                    public void onAdLoadTimeout() {
                        startDelete();
                        dialog.dismiss();
                    }
                }).loadAd();*/

            }
        });

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startDelete(){
        ArrayList<VideoBean> selectVideoBean = videoAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();

        if (selectVideoBean!=null&&!selectVideoBean.isEmpty()){
            for (int i = 0; i < selectVideoBean.size(); i++) {
                if (selectVideoBean.get(i).getSelect()){

                    boolean b = deleteSingleFile(selectVideoBean.get(i).getPath());
                    if (b){
                        integers.add(i);
                    }
                }
            }
            for (int i = 0; i < integers.size(); i++) {
                if (i!=0){
                    videoAdapter.flushedListByPosition(integers.get(i)-i);
                }
                videoAdapter.flushedListByPosition(integers.get(i));
            }

            bt_delete_yes.setVisibility(View.GONE);
            bt_delete_no.setVisibility(View.VISIBLE);

        }
    }

    public void start(ArrayList<VideoBean> list){
        videoAdapter = new VideoAdapter(this, list, new PhotoAdapter.ItemClickListener() {
            @Override
            public void onClick() {
                bt_delete_yes.setVisibility(View.VISIBLE);
                bt_delete_no.setVisibility(View.GONE);
            }

            @Override
            public void Deselect() {
                bt_delete_yes.setVisibility(View.GONE);
                bt_delete_no.setVisibility(View.VISIBLE);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(videoAdapter);
    }

    public void initLoading(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_simple);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        AVLoadingIndicatorView id = dialog.findViewById(R.id.avi);
        id.show();
    }
}