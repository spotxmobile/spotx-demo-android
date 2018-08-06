package com.spotxchange.testapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spotxchange.v4.SpotXAdPlayer;
import com.spotxchange.v4.SpotXAdRequest;
import com.spotxchange.v4.SpotXInterstitialAdPlayer;
import com.spotxchange.v4.datamodel.SpotXAd;
import com.spotxchange.v4.datamodel.SpotXAdGroup;


public class SDKFragment extends Fragment implements SpotXAdPlayer.Observer,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = SDKFragment.class.getSimpleName();

    private ProgressBar _loadingIndicator;
    private Settings _settings;

    private ToggleButton[] _toggles = { null, null, null, null };

    static final int INTERSTITIAL = 0;
    static final int INLINE = 1;
    static final int RESIZABLE = 2;
    static final int DIALOG = 3;

    // MARK: Activity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sdk, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (_settings != null) {
            updatePreferences();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set loading indicator
        _loadingIndicator = (ProgressBar)getView().findViewById(R.id.progressSpinner);
        showLoadingIndicator(false);

        _settings = new Settings(getActivity());
        updatePreferences();

        watchChannelId(_settings);

        ((CheckBox)getView().findViewById(R.id.vpaidBox)).setOnCheckedChangeListener(this);

        // set play button listener
        getView().findViewById(R.id.playButton).setOnClickListener(this);

        // Set up toggle buttons as a multi-select
        _toggles[INTERSTITIAL] = (ToggleButton)getView().findViewById(R.id.selectInterstitial);
        _toggles[INLINE] = (ToggleButton)getView().findViewById(R.id.selectInline);
        _toggles[RESIZABLE] = (ToggleButton)getView().findViewById(R.id.selectResizable);
        _toggles[DIALOG] = (ToggleButton)getView().findViewById(R.id.selectDialog);

        // Deselect all toggle buttons except the current selection
        int selection = _settings.getInt(Settings.KEY_PRESENTATION);
        for (int i = 0; i < _toggles.length; i++) {
            _toggles[i].setChecked(false);
            // Set up button listeners
            _toggles[i].setOnCheckedChangeListener(_toggleListener);
        }
        _toggles[selection].setChecked(true);
    }

    private CompoundButton.OnCheckedChangeListener _toggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // New selection - deselect all others
                for (int i = 0; i < _toggles.length; i++) {
                    CompoundButton button = _toggles[i];
                    if (button == buttonView) {
                        _settings.set(Settings.KEY_PRESENTATION, Integer.valueOf(i));
                    } else {
                        button.setChecked(false);
                    }
                }
            } else {
                // Disallow non-selection by re-checking the button if no others are selected
                boolean any = false;
                for (int i = 0; i < _toggles.length; i++) {
                    if (_toggles[i].isChecked()) {
                        any = true;
                        break;
                    }
                }
                if (!any) {
                    buttonView.setChecked(true);
                }
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && getView() != null) {
            // Appearing after being hidden - check for changes to settings
            updatePreferences();
        }
    }

    // MARK: OnClick

    @Override
    public void onClick(View v) {
        if (_toggles[INTERSTITIAL].isChecked()) {
            _playInterstitial();
        } else if (_toggles[INLINE].isChecked()) {
            _playInline();
        } else if (_toggles[RESIZABLE].isChecked()) {
            _playResizable();
        } else if (_toggles[DIALOG].isChecked()) {
            _playDialog();
        }
    }

    private void _playInterstitial() {
        showLoadingIndicator(true);

        // create interstitial presenter
        SpotXInterstitialAdPlayer player = new SpotXInterstitialAdPlayer();
        player.registerObserver(SDKFragment.this);
        player.load(getActivity());
    }

    private void _playInline() {
        Intent intent = new Intent(getActivity(), SDKInLineActivity.class);
        startActivity(intent);
    }

    private void _playResizable() {
        Intent intent = new Intent(getActivity(), SDKResizableActivity.class);
        startActivity(intent);
    }

    private void _playDialog() {
        Dialog d = new Dialog(getActivity());
        d.show();
    }

    private void showEmptyAdGroupToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showLoadingIndicator(false);
                Toast.makeText(SDKFragment.this.getActivity(), "Empty Ad Group!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "[Group] Empty!");
            }
        });
    }

    // MARK: Helpers

    private void updatePreferences() {
        String channelId = _settings.getString(Settings.KEY_CHANNEL_ID);
        if (channelId.isEmpty()) {
            _settings.set(Settings.KEY_CHANNEL_ID, Settings.DEFAULT_CHANNEL_ID);
            setChannelId(Settings.DEFAULT_CHANNEL_ID);
        } else {
            setChannelId(channelId);
        }
        boolean useVpaid = _settings.getBoolean(Settings.KEY_VPAID);
        CheckBox vpaidBox = (CheckBox)getView().findViewById(R.id.vpaidBox);
        vpaidBox.setChecked(useVpaid);
    }

    private void setChannelId(String channelId) {
        ((EditText)getView().findViewById(R.id.channelText)).setText(channelId);
    }

    private String getChannelId() {
        return ((EditText)getView().findViewById(R.id.channelText)).getText().toString();
    }

    private void watchChannelId(final Settings settings) {
        ((EditText)getView().findViewById(R.id.channelText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                settings.set(Settings.KEY_CHANNEL_ID, editable.toString());
            }
        });
    }

    private void showLoadingIndicator(boolean visible) {
        _loadingIndicator.setVisibility((visible) ? View.VISIBLE : View.INVISIBLE);
        playButtonEnabled(!visible);
    }

    private void playButtonEnabled(boolean enabled) {
        View v = getView();
        if (v != null) {
            v.findViewById(R.id.playButton).setEnabled(enabled);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Toggle VPAID
        _settings.set(Settings.KEY_VPAID, isChecked);
    }

    // MARK: SpotXObserver

    @Override
    public SpotXAdRequest requestForPlayer(@NonNull SpotXAdPlayer player) {
        return _settings.requestWithSettings();
    }

    @Override
    public void onLoadedAds(SpotXAdPlayer player, SpotXAdGroup group, Exception exception) {
        if (group != null && group.ads.size() > 0) {
            player.start();
        } else {
            showEmptyAdGroupToast();
        }
    }

    @Override
    public void onGroupStart(SpotXAdGroup group) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKFragment.this.getActivity(), "Starting group...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "[Group] Start");
            }
        });
    }

    @Override
    public void onStart(SpotXAd ad) {
        Log.d(TAG, "[Ad] Start");
    }

    @Override
    public void onPlay(SpotXAd ad) {
    }

    @Override
    public void onPause(SpotXAd ad) {
    }

    @Override
    public void onComplete(SpotXAd ad) {
        Log.d(TAG, "[Ad] Complete");
    }

    @Override
    public void onSkip(SpotXAd ad) {
        Log.d(TAG, "[Ad] Skip");
    }

    @Override
    public void onUserClose(SpotXAd ad) {
        Log.d(TAG, "[Ad] UserClose");
    }

    @Override
    public void onError(SpotXAd ad, Exception exception) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKFragment.this.getActivity(), "Ad - Error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "[Ad] Error!");
                showLoadingIndicator(false);
            }
        });
    }

    @Override
    public void onGroupComplete(SpotXAdGroup group) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKFragment.this.getActivity(), "Group completed.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "[Group] Complete");
                showLoadingIndicator(false);
            }
        });
    }

    @Override
    public void onTimeUpdate(SpotXAd ad, double elapsed) {
        // do nothing
    }

    @Override
    public void onClick(SpotXAd ad) {
        Log.d(TAG, "[Ad] Clicked");
    }
}

