package com.spotxchange.testapp;

import android.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.spotxchange.v4.adapters.mopub.SpotXRewardedVideo;

import java.util.HashMap;
import java.util.Set;

public class MoPubFragment extends Fragment implements MoPubInterstitial.InterstitialAdListener {

    private Settings _settings;

    private static final String DEFAULT_INTERSTITIAL_ID = "3df9e9bc342b4e5fb70e9fe9d7774317";
    private static final String DEFAULT_REWARDED_ID     = "905a4a3a51484cc589b2f6e8f3466228";

    private MoPubInterstitial _interstitial;

    private ArrayAdapter<CharSequence> _adapterInterstitialChannels;
    private ArrayAdapter<CharSequence> _adapterInterstitialAdUnits;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mopub, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _settings = new Settings(getActivity());

        setupInterstitial();
        setupRewardedVideo();
    }

    // MARK: INTERSTITIAL

    private void setupInterstitial() {
        // Setup the text field for entering the Ad Unit ID
        setupTextFieldForKey(R.id.interstitial_ad_unit, Settings.KEY_MOPUB_INTERSTITIAL, DEFAULT_INTERSTITIAL_ID);

        // Setup the spinner which allows selecting preset channels
        _adapterInterstitialChannels = ArrayAdapter.createFromResource(
                this.getActivity(), R.array.mopub_interstitial_channel_ids, android.R.layout.simple_spinner_item);
        _adapterInterstitialAdUnits = ArrayAdapter.createFromResource(
                this.getActivity(), R.array.mopub_interstitial_ad_units, android.R.layout.simple_spinner_item);
        _adapterInterstitialChannels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) getView().findViewById(R.id.interstitial_spinner);
        spinner.setAdapter(_adapterInterstitialChannels);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String adUnit = (String)_adapterInterstitialAdUnits.getItem(i);
                // Prevent the spinner from showing text itself
                if (view != null) {
                    ((TextView)view).setText("");
                }
                // Update the main text field
                _settings.set(Settings.KEY_MOPUB_INTERSTITIAL, adUnit);
                ((EditText)getView().findViewById(R.id.interstitial_ad_unit)).setText(adUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        getView().findViewById(R.id.interstitial_load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInterstitial();
            }
        });

        getView().findViewById(R.id.interstitial_play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

        enableButton(R.id.interstitial_load_button, true);
        enableButton(R.id.interstitial_play_button, false);
        showLoadingIndicator(R.id.interstitial_progress, false);
    }

    private void loadInterstitial() {
        showLoadingIndicator(R.id.interstitial_progress, true);
        String adUnit = _settings.getString(Settings.KEY_MOPUB_INTERSTITIAL, DEFAULT_INTERSTITIAL_ID);
        _interstitial = new MoPubInterstitial(this.getActivity(), adUnit);

        // Here is where you set the API key
        HashMap<String, Object> extras = new HashMap<>();
        extras.put("apikey", Settings.API_KEY);

        // You can specify request parameters by prefixing them with `spotx_`
        // extras.put("spotx_VPAID", "js");
        // extras.put("spotx_secure", "1");

        // And KVPs or custom macros with `custom_`
        // extras.put("custom_custom[production_year]", "1999");

        _interstitial.setLocalExtras(extras);
        _interstitial.setInterstitialAdListener(this);
        _interstitial.load();
    }

    private void showInterstitial() {
        if (_interstitial != null) {
            _interstitial.show();
            enableButton(R.id.interstitial_load_button, true);
            enableButton(R.id.interstitial_play_button, false);
        }
    }

    private void destroyInterstitial() {
        if (_interstitial != null) {
            _interstitial.destroy();
            _interstitial = null;
            enableButton(R.id.interstitial_load_button, true);
            enableButton(R.id.interstitial_play_button, false);
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        enableButton(R.id.interstitial_load_button, false);
        enableButton(R.id.interstitial_play_button, true);
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
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {}

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {}

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        destroyInterstitial();
        showToast("Interstitial Ad Complete");
    }

    // MARK: Rewarded Video

    private static boolean _mopubInitialized = false;

    private void setupRewardedVideo() {
        setupTextFieldForKey(R.id.rewarded_ad_unit, Settings.KEY_MOPUB_REWARDED, DEFAULT_REWARDED_ID);

        if (!_mopubInitialized) {
            MoPub.initializeRewardedVideo(this.getActivity());
            _mopubInitialized = true;
        }
        MoPub.setRewardedVideoListener(new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                enableButton(R.id.rewarded_load_button, false);
                enableButton(R.id.rewarded_play_button, true);
                showLoadingIndicator(R.id.rewarded_video_progress, false);
                showToast("Rewarded Video Loaded.");
            }

            @Override
            public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                enableButton(R.id.rewarded_load_button, true);
                enableButton(R.id.rewarded_play_button, false);
                showLoadingIndicator(R.id.rewarded_video_progress, false);
                showToast("Rewarded Video Load Failed");
            }

            @Override
            public void onRewardedVideoStarted(@NonNull String adUnitId) {}

            @Override
            public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                enableButton(R.id.rewarded_load_button, true);
                enableButton(R.id.rewarded_play_button, false);
                showLoadingIndicator(R.id.rewarded_video_progress, false);
                showToast("Rewarded Video Playback Error");
            }

            @Override
            public void onRewardedVideoClosed(@NonNull String adUnitId) {}

            @Override
            public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                showToast("Rewarded Video Completed");
            }

        });

        getView().findViewById(R.id.rewarded_load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adUnit = _settings.getString(Settings.KEY_MOPUB_REWARDED, DEFAULT_REWARDED_ID);
                if (adUnit != null) {
                    showLoadingIndicator(R.id.rewarded_video_progress, true);
                    SpotXRewardedVideo.SpotXMediationSettings mediationSettings =
                            new SpotXRewardedVideo.SpotXMediationSettings.Builder()
                                    .withApiKey(Settings.API_KEY)
                                    .build();
                    MoPub.loadRewardedVideo(adUnit, mediationSettings);
                }
            }
        });

        getView().findViewById(R.id.rewarded_play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adUnit = _settings.getString(Settings.KEY_MOPUB_REWARDED, DEFAULT_REWARDED_ID);
                if (adUnit != null) {
                    MoPub.showRewardedVideo(adUnit);
                }
                enableButton(R.id.rewarded_load_button, true);
                enableButton(R.id.rewarded_play_button, false);
            }
        });

        enableButton(R.id.rewarded_load_button, true);
        enableButton(R.id.rewarded_play_button, false);
        showLoadingIndicator(R.id.rewarded_video_progress, false);
    }

    // MARK: HELPERS

    private void setupTextFieldForKey(@IdRes int fieldId, final String settingsKey, String defaultValue) {
        String adUnit = _settings.getString(settingsKey, defaultValue);
        ((EditText)getView().findViewById(fieldId)).setText(adUnit);
        ((EditText)getView().findViewById(fieldId)).addTextChangedListener(new TextWatcher() {
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

    private void enableButton(final int buttonId, final boolean enable) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getView().findViewById(buttonId).setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
                getView().findViewById(buttonId).setEnabled(enable);
            }
        });
    }

    private void showLoadingIndicator(final int progressId, final boolean show) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getView().findViewById(progressId).setVisibility((show) ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void showToast(final CharSequence text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MoPubFragment.this.getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
