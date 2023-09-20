package net.management.utility.file.manager.ad;


import static net.management.utility.file.manager.constant.MyAppApiConfig.INTERSTITIAL_ID;

import android.app.Activity;
import android.content.Context;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;

import net.management.utility.file.manager.App;
import net.management.utility.file.manager.util.MyUtil;

public class InterstitialAd {
    public static ATInterstitial mInterstitialAd;
    private static Context context;
    private App.OnShowAdCompleteListener listener;

    public InterstitialAd(Context context, App.OnShowAdCompleteListener listener){
        this.listener = listener;
        InterstitialAd.context = context;
    }

    public void loadAd() {
        if (mInterstitialAd == null) {
            mInterstitialAd = new ATInterstitial(context, INTERSTITIAL_ID);
        }

        mInterstitialAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                listener.onAdLoaded();

                ATInterstitial.entryAdScenario(INTERSTITIAL_ID, null);
                if (mInterstitialAd.isAdReady()) {
                    mInterstitialAd.show((Activity) context);
                }
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                //注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                //AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                listener.onFailedToLoad();
                MyUtil.MyLog("广告加载出错："+adError);

            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo atAdInfo) {
                listener.onClicked();
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo atAdInfo) {
                //ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                //请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info
                listener.onShowAdComplete();
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                listener.TurnoffAds();
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {
                //建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                mInterstitialAd.load();
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                //AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
           //     listener.onFailedToLoad();
            }
        });

        mInterstitialAd.load();
    }
}
