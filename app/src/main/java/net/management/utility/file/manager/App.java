package net.management.utility.file.manager;


import static net.management.utility.file.manager.constant.MyAppApiConfig.TopOnAppID;
import static net.management.utility.file.manager.constant.MyAppApiConfig.TopOnAppKey;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.anythink.core.api.ATSDK;
import com.tencent.mmkv.MMKV;

import net.management.utility.file.manager.util.MyUtil;


/** Application class that initializes, loads and show ads when activities change states. */
public class App extends Application
        implements ActivityLifecycleCallbacks {

    private int count=0;
    public  static Boolean isForground = true;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        MMKV.initialize(this);
        initDate();
        initGoogleAds();

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void initDate(){
        ATSDK.init(getApplicationContext(), TopOnAppID, TopOnAppKey);//初始化SDK
    }
    //初始化google ads
    public void initGoogleAds(){
      //  initAds();

    }



    /**
     * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
     */

    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

        count++;
        if (!isForground){
            //后台回到前台
            isForground = true;


        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        MyUtil.MyLog("Resumed--"+activity.getClass().getName());

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        MyUtil.MyLog("Paused");

    }



    @Override
    public void onActivityStopped(@NonNull Activity activity) {

        count--;
        if (count==0){
            isForground = false;
            //切后台

        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        MyUtil.MyLog("onActivitySaveInstanceState");


    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }


    /**
     * 广告加载完成以及关闭回调
     */
    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
        void TurnoffAds();

        void onFailedToLoad();

        void onAdFailedToShow();

        void onAdLoaded();
        void onClicked();
        void onAdLoadTimeout();
    }


}