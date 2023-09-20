package net.management.utility.file.manager.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.wang.avi.AVLoadingIndicatorView;

import net.management.utility.file.manager.App;
import net.management.utility.file.manager.R;
import net.management.utility.file.manager.ad.InterstitialAd;

public class StartAct extends AppCompatActivity {

    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_start);
        initUI();
        startInterstitialAd();

    }

    public void initUI(){
        avi = findViewById(R.id.avi);

        avi.show();
    }

    public void startInterstitialAd(){
        new InterstitialAd(StartAct.this, new App.OnShowAdCompleteListener() {
            @Override
            public void onShowAdComplete() {

            }

            @Override
            public void TurnoffAds() {
                startMain();
            }

            @Override
            public void onFailedToLoad() {
                startMain();
            }

            @Override
            public void onAdFailedToShow() {
                startMain();
            }

            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onClicked() {

            }

            @Override
            public void onAdLoadTimeout() {
                startMain();
            }
        }).loadAd();
    }

    public void startMain(){
        startActivity(new Intent(StartAct.this,MainAct.class));
        finish();
    }
}