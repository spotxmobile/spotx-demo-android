package com.spotxchange.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;

import com.spotxchange.sdk.mopubintegration.SpotxRewardedVideo.SpotxMediationSettings;

import net.hockeyapp.android.CrashManager;

import java.util.Set;

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
        setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_load_rewarded_video:
                MoPub.loadRewardedVideo("5f56564aa4b1487cabb9b59cc3134b93");
                break;
            case R.id.button_show_rewarded_video:
                MoPub.showRewardedVideo("5f56564aa4b1487cabb9b59cc3134b93");
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
                break;
        }
    }

    private void checkForCrashes() {
        if (!BuildConfig.DEBUG) {
            CrashManager.register(this, getString(R.string.HOCKEY_API_KEY));
        }
    }

    private void setButtonEnabledStatus(int iButtonId, boolean bIsEnabled){
        findViewById(iButtonId).setEnabled(bIsEnabled);
        int iColorId = bIsEnabled ? R.color.spotxblue : R.color.gray;
        ((Button) findViewById(iButtonId)).setTextColor(getResources().getColor(iColorId));
    }

    private void setRewardedVideoListener(){
        rewardedVideoListener = new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(String adUnitId) {
                showToast("Video loaded!");
                setButtonEnabledStatus(R.id.button_show_rewarded_video, true);
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
