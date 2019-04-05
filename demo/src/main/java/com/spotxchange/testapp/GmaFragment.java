package com.spotxchange.testapp;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.spotxchange.internal.utility.SPXLog;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class GmaFragment extends Fragment {

    private Settings _settings;

    private static final String APP_ID                  = "ca-app-pub-9627326094741214~4252533337";
    private static final String DEFAULT_INTERSTITIAL_ID = "ca-app-pub-9627326094741214/2249735261";
    private static final String DEFAULT_REWARDED_ID     = "ca-app-pub-9627326094741214/3088221946";

    private EditText _interstitialId;
    private EditText _rewardedId;

    private Button _buttonInterstitial;
    private Button _buttonRewarded;

    private InterstitialAd _interstitialAd;
    private RewardedVideoAd _rewardedAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gma, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _settings = new Settings(getActivity());

        _interstitialId = (EditText)getView().findViewById(R.id.interstitialId);
        _rewardedId = (EditText)getView().findViewById(R.id.rewardedId);

        setupTextFieldForKey(_interstitialId, Settings.KEY_GMA_INTERSTITIAL, DEFAULT_INTERSTITIAL_ID);
        setupTextFieldForKey(_rewardedId, Settings.KEY_GMA_REWARDED, DEFAULT_REWARDED_ID);

        _buttonInterstitial = (Button)getView().findViewById(R.id.buttonInterstitial);
        _buttonInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });

        _buttonRewarded = (Button)getView().findViewById(R.id.buttonRewarded);
        _buttonRewarded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewarded();
            }
        });

    }

    // MARK: AD PLAYBACK

    private void showInterstitial() {
        MobileAds.initialize(getActivity(), APP_ID);
        _interstitialAd = new InterstitialAd(getActivity());
        _interstitialAd.setAdUnitId(_settings.getString(Settings.KEY_GMA_INTERSTITIAL, DEFAULT_INTERSTITIAL_ID));

        _interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                _interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(getActivity(), "Failed to load ad: " + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getActivity(), "Ad Complete", Toast.LENGTH_SHORT).show();
            }
        });

        AdRequest request = getAdRequest();
        if (request != null) {
            _interstitialAd.loadAd(request);
        }
    }

    private void showRewarded() {
        MobileAds.initialize(getActivity(), APP_ID);
        _rewardedAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        _rewardedAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                _rewardedAd.show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Toast.makeText(getActivity(), "Failed to load ad: " + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(getActivity(),
                        "Got reward: " + rewardItem.getType()
                                + " (" + rewardItem.getAmount() + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });

        AdRequest request = getAdRequest();
        if (request != null) {
            String adUnitId = _settings.getString(Settings.KEY_GMA_REWARDED, DEFAULT_REWARDED_ID);
            _rewardedAd.loadAd(adUnitId, request);
        }
    }


    // MARK: HELPERS

    /** Sets up an ad request, making sure that the current device is configured as a test device. */
    private AdRequest getAdRequest() {
        String deviceId = getDeviceId();
        if (deviceId == null) {
            Toast.makeText(getActivity(), "Failed to get device ID", Toast.LENGTH_SHORT);
            return null;
        }
        AdRequest request = new AdRequest.Builder().addTestDevice(deviceId).build();
        if (!request.isTestDevice(getActivity())) {
            Toast.makeText(getActivity(), "Failed to register test device", Toast.LENGTH_SHORT);
            return null;
        }
        return request;
    }

    /** Gets Android device ID in a way that matches Google Ads. */
    private String getDeviceId() {
        Context var0 = getActivity();
        ContentResolver var1;
        String var10000 =
                (var1 = var0.getContentResolver()) == null
                        ? null
                        : android.provider.Settings.Secure.getString(var1, "android_id");
        MessageDigest var2;
        try {
            (var2 = MessageDigest.getInstance("MD5")).update(var10000.getBytes());
            return String.format(Locale.US, "%032X", new BigInteger(1, var2.digest()));
        } catch (NoSuchAlgorithmException | ArithmeticException e) {
            return null;
        }
    }

    private void setupTextFieldForKey(EditText field, final String settingsKey, String defaultValue) {
        String adUnit = _settings.getString(settingsKey, defaultValue);
        field.setText(adUnit);
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                _settings.set(settingsKey, s.toString());
            }
        });
    }

}
