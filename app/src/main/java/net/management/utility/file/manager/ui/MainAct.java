package net.management.utility.file.manager.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import net.management.utility.file.manager.R;
import net.management.utility.file.manager.bean.ImageBean;
import net.management.utility.file.manager.bean.VideoBean;
import net.management.utility.file.manager.bean.ZipBean;
import net.management.utility.file.manager.util.MyUtil;
import net.management.utility.file.manager.util.QueryTask;
import net.management.utility.file.manager.util.TimerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainAct extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar scanProgressBar;
    private int progressStatus = 0;
    private int VideoCounts = 0;
    private ArrayList<VideoBean> videoBeans;
    private ArrayList<ZipBean> zipBeans;
    private int REQUEST_CODE = 10086;
    private TextView image_text, video_text, zip_text,bt_text,textMemory;
    private RelativeLayout image_layout,video_layout,zip_layout;
    private ArrayList<ImageBean> imageBeans;
    private final static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    private TimerUtil timerUtil;
    private TimerUtil flashTimerUtil;
    private int speed = 100;
    private boolean isStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        initUI();
        initData();
        initListener();

    }

    public void initUI() {
        scanProgressBar = findViewById(R.id.scanProgressBar);
        image_text = findViewById(R.id.image_text);
        video_text = findViewById(R.id.video_text);
        zip_text = findViewById(R.id.zip_text);
        image_layout = findViewById(R.id.image_layout);
        video_layout = findViewById(R.id.video_layout);
        zip_layout = findViewById(R.id.zip_layout);
        bt_text = findViewById(R.id.bt_text);
        textMemory = findViewById(R.id.textMemory);


    }

    public void initListener(){
        image_layout.setOnClickListener(this);
        video_layout.setOnClickListener(this);
        zip_layout.setOnClickListener(this);
        scanProgressBar.setOnClickListener(this);
    }
    public void initData() {
        // GetImagesPath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyUtil.MyLog("内存大小"+getTotalMemory());

                runOnUiThread(()->{
                    textMemory.setText(getTotalMemory()+" Memory Used");
                });

            }
        }).start();

        requestPermission();

    }
    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(this, blockSize * totalBlocks);
    }

    //系统总存储大小
    private String getTotalMemory() {
        //获取ROM内存信息
        //调用该类来获取磁盘信息（而getDataDirectory就是内部存储）
        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totalCounts = statFs.getBlockCountLong();//总共的block数
        long availableCounts = statFs.getAvailableBlocksLong() ; //获取可用的block数
        long size = statFs.getBlockSizeLong(); //每格所占的大小，一般是4KB==
        long availROMSize = availableCounts * size;//可用内部存储大小
        long totalROMSize = totalCounts *size; //内部存储总大小

        long l = totalROMSize - availROMSize;
        return (Formatter.formatFileSize(this, l)+" / "+ Formatter.formatFileSize(this, totalROMSize));
    }



    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                GetImagesPath();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                GetImagesPath();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {
            GetImagesPath();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                GetImagesPath();
            } else {
              //  Toast.makeText(this, "请给存储权限，更新应用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                GetImagesPath();
            } else {
            //    Toast.makeText(this, "请给存储权限，更新应用", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void startScan() {
        progressStatus=0;
        timerUtil = new TimerUtil(10, speed, new TimerUtil.TimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isStop) {
                    progressStatus++;
                    scanProgressBar.setProgress(progressStatus);
                }
            }

            @Override
            public void onFinish() {

            }
        });

        timerUtil.start();
    }

    public void startFlashScan() {

        flashTimerUtil = new TimerUtil(2, 1, new TimerUtil.TimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                progressStatus++;
                scanProgressBar.setProgress(progressStatus);
            }

            @Override
            public void onFinish() {
                isStop = false;
            }
        });

        flashTimerUtil.start();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.scanProgressBar) {
            progressStatus = 0;
            requestPermission();
        }
        if (v.getId() == R.id.image_layout){
            Intent intent = new Intent(MainAct.this, PhotoAct.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.video_layout){
            Intent intent = new Intent(MainAct.this, VideoAct.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.zip_layout){
            Intent intent = new Intent(MainAct.this, ZipAct.class);
            startActivity(intent);
        }

    }

    public void GetImagesPath() {
        bt_text.setText("Scanning");

        image_layout.setClickable(false);
        video_layout.setClickable(false);
        zip_layout.setClickable(false);

        startScan();
        imageBeans = new ArrayList<>();
        videoBeans = new ArrayList<>();
        zipBeans = new ArrayList<>();

        new QueryTask(MainAct.this, new QueryTask.QueryListener() {
            @Override
            public void onQueryComplete(ArrayList<ArrayList> list) {
                isStop = true;
                startFlashScan();
                bt_text.setText("Start Scan");

                imageBeans = list.get(0);
                videoBeans = list.get(1);
                zipBeans = list.get(2);

                String imageGson = new Gson().toJson(imageBeans);
                MMKV.defaultMMKV().encode("imageList",imageGson);

                String videoGson = new Gson().toJson(videoBeans);
                MMKV.defaultMMKV().encode("videoGson",videoGson);

                String zipGson = new Gson().toJson(zipBeans);
                MMKV.defaultMMKV().encode("zipGson",zipGson);

                image_text.setText(imageBeans.size() + " items");

                video_text.setText(videoBeans.size() + " items");

                zip_text.setText(zipBeans.size()+" items");


                image_layout.setClickable(true);
                video_layout.setClickable(true);
                zip_layout.setClickable(true);
            }
        }).execute();

    }


    /**
     * ROM内存大小，返回 64G/128G/256G/512G
     * @return
     */
    private static String getTotalRom() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        long GB = 1024 * 1024 * 1024;
        final long[] deviceRomMemoryMap = {2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
        String[] displayRomSize = {"2GB","4GB","8GB","16GB","32GB","64GB","128GB","256GB","512GB","1024GB","2048GB"};
        int i;
        for(i = 0 ; i < deviceRomMemoryMap.length; i++) {
            if(size <= deviceRomMemoryMap[i]) {
                break;
            }
            if(i == deviceRomMemoryMap.length) {
                i--;
            }
        }
        return displayRomSize[i];
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }
}