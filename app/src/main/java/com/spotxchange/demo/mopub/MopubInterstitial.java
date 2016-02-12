package com.spotxchange.demo.mopub;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.spotxchange.sdk.mopubintegration.SpotXInterstitial;

import net.hockeyapp.android.CrashManager;

import java.util.Set;

import com.spotxchange.demo.R;
import com.spotxchange.sdk.mopubintegration.SpotxRewardedVideo;

public class MopubInterstitial extends Activity implements MoPubInterstitial.InterstitialAdListener {
    private MoPubInterstitial _interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_interstitial);

        setButtonEnabledStatus(R.id.button_show_interstitial, false);

        findViewById(R.id.button_load_interstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitialAd();
            }
        });

        findViewById(R.id.button_show_interstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd();
            }
        });
    }

    private void loadInterstitialAd() {
        String adUnitId = ((EditText)findViewById(R.id.edittext_mopub_ad_unit_id)).getText().toString();

        _interstitial = new MoPubInterstitial(this, adUnitId);
        if (null != _interstitial) {
            _interstitial.setInterstitialAdListener(this);
            _interstitial.load();
            setButtonEnabledStatus(R.id.button_show_interstitial, false);
            setButtonText(R.id.button_show_interstitial, R.string.loading_text);
        }
    }

    private void showInterstitialAd() {
        if (null != _interstitial) {
            _interstitial.show();
            setButtonEnabledStatus(R.id.button_show_interstitial, false);
        }
    }

    private void setButtonEnabledStatus(int iButtonId, boolean bIsEnabled){
        findViewById(iButtonId).setEnabled(bIsEnabled);
        int iColorId = bIsEnabled ? R.color.spotxblue : R.color.gray;
        ((Button) findViewById(iButtonId)).setTextColor(getResources().getColor(iColorId));
    }

    private void setButtonText(int buttonId, int buttonText){
        ((Button) findViewById(buttonId)).setText(buttonText);
    }

    private void showToast(CharSequence text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void destroy() {
        if (null != _interstitial) {
            _interstitial.destroy();
            _interstitial = null;

            setButtonText(R.id.button_show_interstitial, R.string.launch_text);
            setButtonEnabledStatus(R.id.button_show_interstitial, false);
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        setButtonText(R.id.button_show_interstitial, R.string.launch_text);
        setButtonEnabledStatus(R.id.button_show_interstitial, true);
        showToast("Ad Loaded");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        destroy();
        showToast("Ad Error");
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        destroy();
        showToast("Ad Complete");
    }

}
