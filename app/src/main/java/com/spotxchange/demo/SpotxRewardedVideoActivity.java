package com.spotxchange.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.android.SpotxAdView;
import com.spotxchange.sdk.mopubintegration.SpotxRewardedVideo.SpotxMediationSettings;

import net.hockeyapp.android.CrashManager;

import java.util.Set;

/**
 * Created by zmiller on 1/12/16.
 */
public class SpotxRewardedVideoActivity extends Activity implements View.OnClickListener {

    private MoPubRewardedVideoListener rewardedVideoListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rewarded_video);
        checkForCrashes();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        MoPub.initializeRewardedVideo(
                this,
                new SpotxMediationSettings
                        .Builder()
                        .withChannelId("85394")
                        .build());

        MoPub.onCreate(this);
        setRewardedVideoListener();
        MoPub.setRewardedVideoListener(rewardedVideoListener);

        findViewById(R.id.button_show_rewarded_video).setEnabled(false);
        ((Button) findViewById(R.id.button_show_rewarded_video)).setTextColor(getResources().getColor(R.color.gray));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_load_rewarded_video:
                MoPub.loadRewardedVideo("5f56564aa4b1487cabb9b59cc3134b93");
                break;
            case R.id.button_show_rewarded_video:
                MoPub.showRewardedVideo("5f56564aa4b1487cabb9b59cc3134b93");

                findViewById(R.id.button_show_rewarded_video).setEnabled(false);
                ((Button) findViewById(R.id.button_show_rewarded_video)).setTextColor(getResources().getColor(R.color.gray));
                break;
        }
    }

    private void checkForCrashes() {
        if (!BuildConfig.DEBUG) {
            CrashManager.register(this, getString(R.string.HOCKEY_API_KEY));
        }
    }

    private void setRewardedVideoListener(){
        rewardedVideoListener = new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(String adUnitId) {
                showToast("Video loaded!");
                findViewById(R.id.button_show_rewarded_video).setEnabled(true);
                ((Button) findViewById(R.id.button_show_rewarded_video)).setTextColor(getResources().getColor(R.color.spotxblue));
            }

            @Override
            public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {
                showToast("Video load failed!");
            }

            @Override
            public void onRewardedVideoStarted(String adUnitId) {

            }

            @Override
            public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {
                showToast("Video playback Error!");
            }

            @Override
            public void onRewardedVideoClosed(String adUnitId) {
                showToast("Video closed!");
            }

            @Override
            public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {
                showToast("Video completed, you recieved " +
                        Integer.toString(reward.getAmount())
                        + " " + reward.getLabel());
            }

            private void showToast(CharSequence text){
                Context context = getApplicationContext();
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
