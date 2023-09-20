package net.management.utility.file.manager.ui;

import static net.management.utility.file.manager.util.FileUtil.deleteSingleFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.wang.avi.AVLoadingIndicatorView;

import net.management.utility.file.manager.App;
import net.management.utility.file.manager.R;
import net.management.utility.file.manager.ad.OpenAd;
import net.management.utility.file.manager.adapter.PhotoAdapter;
import net.management.utility.file.manager.bean.ImageBean;
import net.management.utility.file.manager.util.MyUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class PhotoAct extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private Button bt_delete_no,bt_delete_yes;
    private ArrayList<ImageBean> imageBeans;
    private ImageView ic_back;
    private FrameLayout ad_layout;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photo);
      //  initLoading();
        initUI();
        initData();
        initListener();
    }
    public void initData(){
        String imageList = MMKV.defaultMMKV().decodeString("imageList");

        imageBeans = new Gson().fromJson(imageList,new TypeToken<ArrayList<ImageBean>>(){}.getType());

        start(false,imageBeans);
    }
    public void initUI(){
        recyclerView = findViewById(R.id.recyclerView);
        bt_delete_no = findViewById(R.id.bt_delete_no);
        bt_delete_yes = findViewById(R.id.bt_delete_yes);
        ic_back = findViewById(R.id.ic_back);
        ad_layout = findViewById(R.id.ad_layout);


    }

    public void initListener(){
        bt_delete_yes.setOnClickListener(this);
        ic_back.setOnClickListener(this);
    }

    public void start(Boolean b, ArrayList<ImageBean> list){


        photoAdapter = new PhotoAdapter(this, list, new PhotoAdapter.ItemClickListener() {
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
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_delete_yes){
            
            startDelete();
/*            new OpenAd(PhotoAct.this, ad_layout, new App.OnShowAdCompleteListener() {
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

        if (v.getId()==R.id.ic_back){
            finish();
        }
    }
    public void startDelete(){
        ArrayList<ImageBean> selectImageBeans = photoAdapter.getSelectItems();
        ArrayList<Integer> integers = new ArrayList<>();

        for (int i = 0; i < selectImageBeans.size(); i++) {
            if (selectImageBeans.get(i).getSelect()){
                MyUtil.MyLog("开始删除"+i);
                boolean b = deleteSingleFile(selectImageBeans.get(i).getPath());
                if (b){
                    integers.add(i);
                }
            }
        }

        for (int i = 0; i < integers.size(); i++) {
            if (i!=0){
                photoAdapter.flushedListByPosition(integers.get(i)-i);
            }
            photoAdapter.flushedListByPosition(integers.get(i));
        }

        bt_delete_yes.setVisibility(View.GONE);
        bt_delete_no.setVisibility(View.VISIBLE);
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
