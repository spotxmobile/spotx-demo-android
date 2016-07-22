package com.spotxchange.mopub.demo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.spotxchange.sdk.mopubintegration.SpotXRewardedVideo;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private MoPubInterstitial _mopubInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMoPubInterstitial();
        setupMoPubRewardedVideo();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MoPub.onDestroy(this);
    }

    private void setupMoPubInterstitial() {
        // interstitial load button
        findViewById(R.id.interstitial_load_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInterstitialAd();
            }
        });

        // interstitial play button
        findViewById(R.id.interstitial_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInterstitialAd();
            }
        });

        enableButton(R.id.interstitial_play_btn, false);
        showLoadingIndicator(R.id.interstitial_progress, false);
    }

    private void setupMoPubRewardedVideo() {
        MoPub.initializeRewardedVideo(this);
        MoPub.onCreate(this);
        MoPub.setRewardedVideoListener(_rewardedAdListener);

        // rewarded video load button
        findViewById(R.id.rewarded_load_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRewardedVideoAd();
            }
        });

        // rewarded video play button
        findViewById(R.id.rewarded_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRewardedVideoAd();
            }
        });

        enableButton(R.id.rewarded_play_btn, false);
        showLoadingIndicator(R.id.rewarded_progress, false);
    }

    // Interstitial Methods

    private void loadInterstitialAd() {
        String adUnit = getEditText(R.id.interstitial_ad_unit_id);
        if(adUnit != null) {
            showLoadingIndicator(R.id.interstitial_progress, true);
            _mopubInterstitial = new MoPubInterstitial(this, adUnit);
            _mopubInterstitial.setInterstitialAdListener(_interstitialAdListener);
            _mopubInterstitial.load();
        }
        else{
            showToast("Interstitial Ad Unit Is Empty");
        }
    }

    private void playInterstitialAd() {
        if(_mopubInterstitial != null) {
            _mopubInterstitial.show();
            enableButton(R.id.interstitial_play_btn, false);
        }
    }

    private void destroyInterstitial() {
        if(_mopubInterstitial != null) {
            _mopubInterstitial.destroy();
            _mopubInterstitial = null;
            enableButton(R.id.interstitial_play_btn, false);
        }
    }

    // Rewarded Video Methods

    private void loadRewardedVideoAd() {
        String adUnit = getEditText(R.id.rewarded_ad_unit_id);
        String channelId = getEditText(R.id.rewarded_channel_id);
        if(adUnit != null && channelId != null){
            showLoadingIndicator(R.id.rewarded_progress, true);
            SpotXRewardedVideo.SpotXMediationSettings mediationSettings =
                    new SpotXRewardedVideo.SpotXMediationSettings.Builder().withChannelId(channelId).build();
            MoPub.loadRewardedVideo(adUnit, mediationSettings);
        }
        else {
            showToast("Rewarded Ad Unit or Channel Is Empty");
        }
    }

    private void playRewardedVideoAd() {
        String adUnit = getEditText(R.id.rewarded_ad_unit_id);
        if(adUnit != null) {
            MoPub.showRewardedVideo(adUnit);
        }
        enableButton(R.id.rewarded_play_btn, false);
    }

    // MoPub Interstitial Listener

    MoPubInterstitial.InterstitialAdListener _interstitialAdListener = new MoPubInterstitial.InterstitialAdListener() {
        @Override
        public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
            enableButton(R.id.interstitial_play_btn, true);
            showLoadingIndicator(R.id.interstitial_progress, false);
            showToast("Interstitial Ad Loaded");
        }

        @Override
        public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
            showLoadingIndicator(R.id.interstitial_progress, false);
            destroyInterstitial();
            showToast("Interstitial Ad Error");
        }

        @Override
        public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
            destroyInterstitial();
            showToast("Interstitial Ad Complete");
        }

        @Override
        public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
            // do nothing
        }

        @Override
        public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
            // do nothing
        }
    };

    // MoPub Rewarded Video Listener

    MoPubRewardedVideoListener _rewardedAdListener = new MoPubRewardedVideoListener() {
        @Override
        public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
            enableButton(R.id.rewarded_play_btn, true);
            showLoadingIndicator(R.id.rewarded_progress, false);
            showToast("Rewarded Ad Loaded.");
        }

        @Override
        public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
            enableButton(R.id.rewarded_play_btn, false);
            showLoadingIndicator(R.id.rewarded_progress, false);
            showToast("Rewarded Ad Load Failed");
        }

        @Override
        public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
            enableButton(R.id.rewarded_play_btn, false);
            showLoadingIndicator(R.id.rewarded_progress, false);
            showToast("Rewarded Ad Playback Error");
        }

        @Override
        public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
            showToast("Rewarded Ad Completed");
        }

        @Override
        public void onRewardedVideoStarted(@NonNull String adUnitId) {
            // do nothing
        }

        @Override
        public void onRewardedVideoClosed(@NonNull String adUnitId) {
            // do nothing
        }

    };

    // MARK: Helpers

    private void enableButton(int iButtonId, boolean enable){
        findViewById(iButtonId).setEnabled(enable);
    }

    private void showLoadingIndicator(int progressId, boolean show){
        findViewById(progressId).setVisibility((show) ? View.VISIBLE : View.INVISIBLE);
    }

    private void showToast(CharSequence text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    private String getEditText(int editTextId) {
        try {
            EditText textBox = ((EditText) findViewById(editTextId));
            return (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? null : textBox.getText().toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
