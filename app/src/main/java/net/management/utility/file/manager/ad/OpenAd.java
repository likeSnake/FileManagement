package net.management.utility.file.manager.ad;


import static net.management.utility.file.manager.App.isForground;
import static net.management.utility.file.manager.constant.MyAppApiConfig.OpenADID;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;

import net.management.utility.file.manager.App;
import net.management.utility.file.manager.util.MyUtil;


public class OpenAd {
    public static ATInterstitial mInterstitialAd;
    private static Context context;
    private App.OnShowAdCompleteListener listener;
    private static ATSplashAd splashAd;
    private FrameLayout container;

    public OpenAd(Context context, FrameLayout container, App.OnShowAdCompleteListener listener){
        this.listener = listener;
        this.container = container;
        OpenAd.context = context;
    }

    ATSplashExListener AdListener = new ATSplashExListener() {
        @Override
        public void onDeeplinkCallback(ATAdInfo atAdInfo, boolean b) {

        }

        @Override
        public void onDownloadConfirm(Context context, ATAdInfo atAdInfo, ATNetworkConfirmInfo atNetworkConfirmInfo) {

        }
        @Override
        public void onAdLoaded(boolean isTimeout) {
            listener.onAdLoaded();
            //加载未超时时
            if(!isTimeout){
                //当前Activity处于前台时进行广告展示
                if(isForground){
                    //container大小至少占屏幕75%
                    splashAd.show((Activity) context, container);
                }
            }
        }

        @Override
        public void onAdLoadTimeout() {
            //加载超时后，直接进入主界面
        //    jumpToMainActivity();
            listener.onAdLoadTimeout();
        }

        @Override
        public void onNoAdError(AdError adError) {
            //加载失败直接进入主界面
         //   jumpToMainActivity();
            listener.onFailedToLoad();
            MyUtil.MyLog("广告加载出错："+adError);

        }

        @Override
        public void onAdShow(ATAdInfo entity) {
            listener.onShowAdComplete();
        }

        @Override
        public void onAdClick(ATAdInfo atAdInfo) {

        }

        @Override
        public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {
            //=1无区分 =2跳过结束 =3倒计时结束 =4点击广告结束 =99开屏展示失败
            if(splashAdExtraInfo.getDismissType()== ATAdConst.DISMISS_TYPE.SHOWFAILED){
            }
            //开屏广告展示关闭后进入主界面
            //注意：部分平台跳转落地页后倒计时不暂停，即使在看落地页，倒计时结束后仍然会回调onAdDismiss
            //因此在页面跳转时需要特殊处理，详情参考下方示例代码
         //   jumpToMainActivity();

            listener.TurnoffAds();
        }
    };

    public void loadAd() {
        ATInterstitial.entryAdScenario(OpenADID, null);
       // if (splashAd == null) {
            splashAd = new ATSplashAd(context, OpenADID, AdListener, 5000,null);
      //  }

        splashAd.loadAd();
    }


}
