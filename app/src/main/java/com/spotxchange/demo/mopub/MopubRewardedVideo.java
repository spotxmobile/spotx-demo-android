package com.spotxchange.demo.mopub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.spotxchange.demo.BuildConfig;
import com.spotxchange.demo.R;
import com.spotxchange.sdk.mopubintegration.SpotxRewardedVideo.SpotxMediationSettings;

import net.hockeyapp.android.CrashManager;

import java.util.Set;

public class MopubRewardedVideo extends Activity {
    private static boolean _isVideoDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_rewardedvideo);
        checkForCrashes();

        _isVideoDisplayed = false;
        setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
        setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
        initRewardedVideo();

        findViewById(R.id.button_load_rewarded_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoPub.loadRewardedVideo(getAdUnitIdFromEditText(), new SpotxMediationSettings.Builder()
                        .withChannelId(getChannelIdFromEditText()).build());
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
                setButtonText(R.id.button_show_rewarded_video, R.string.loading_text);
            }
        });

        findViewById(R.id.button_show_rewarded_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoPub.showRewardedVideo(getAdUnitIdFromEditText());
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(_isVideoDisplayed){
            MoPub.onBackPressed(this);
            setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
            setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
            _isVideoDisplayed = false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MoPub.onDestroy(this);
    }

    private void setButtonEnabledStatus(int iButtonId, boolean bIsEnabled){

        findViewById(iButtonId).setEnabled(bIsEnabled);
        int iColorId = bIsEnabled ? R.color.spotxblue : R.color.gray;
        ((Button) findViewById(iButtonId)).setTextColor(getResources().getColor(iColorId));
    }

    private void setButtonText(int buttonId, int buttonText){
        ((Button) findViewById(buttonId)).setText(buttonText);
    }

    private void initRewardedVideo(){

        MoPub.initializeRewardedVideo(this);
        MoPub.onCreate(this);
        MoPub.setRewardedVideoListener(getRewardedVideoListener());
    }

    private String getAdUnitIdFromEditText() {

        String result = "";
        try {
            EditText textBox = ((EditText) findViewById(R.id.edittext_mopub_ad_unit_id));
            return (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() :
                    textBox.getText().toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getChannelIdFromEditText(){

        String result = "";
        try {
            EditText textBox = ((EditText) findViewById(R.id.edittext_spotx_channel_id));
            return (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() :
                    textBox.getText().toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    private void checkForCrashes() {
        if (!BuildConfig.DEBUG) {
            CrashManager.register(this, getString(R.string.HOCKEY_API_KEY));
        }
    }

    private MoPubRewardedVideoListener getRewardedVideoListener(){

        return new MoPubRewardedVideoListener() {

            @Override
            public void onRewardedVideoLoadSuccess(String adUnitId) {

                showToast("Video loaded.");
                _isVideoDisplayed = false;
                setButtonEnabledStatus(R.id.button_show_rewarded_video, true);
                setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
            }

            @Override
            public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {

                showToast("Video load failed.");
                _isVideoDisplayed = false;
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
                setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
            }

            @Override
            public void onRewardedVideoStarted(String adUnitId) {
                _isVideoDisplayed = true;
            }

            @Override
            public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {

                showToast("Video playback Error.");
                _isVideoDisplayed = false;
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
                setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
            }

            @Override
            public void onRewardedVideoClosed(String adUnitId) {

                _isVideoDisplayed = false;
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
                setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
            }

            @Override
            public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {

                showToast("Video completed, you recieved a reward.");
                _isVideoDisplayed = false;
                setButtonEnabledStatus(R.id.button_show_rewarded_video, false);
                setButtonText(R.id.button_show_rewarded_video, R.string.launch_text);
            }

            private void showToast(CharSequence text){

                Context context = getApplicationContext();
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
