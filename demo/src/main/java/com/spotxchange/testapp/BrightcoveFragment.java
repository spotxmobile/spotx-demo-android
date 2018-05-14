package com.spotxchange.testapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class BrightcoveFragment extends Fragment implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Settings _settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brightcove, container, false);
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
        super.onActivityCreated(null);

        _settings = new Settings(getActivity());
        updatePreferences();

        watchChannelId(_settings);

        ((CheckBox)getView().findViewById(R.id.vpaidBox)).setOnCheckedChangeListener(this);

        // set play button listener
        getView().findViewById(R.id.playButton).setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && getView() != null) {
            // Appearing after being hidden - check for changes to settings
            updatePreferences();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Toggle VPAID
        _settings.set(Settings.KEY_VPAID, Boolean.valueOf(isChecked));
    }

    /** Play button clicked. */
    @Override
    public void onClick(View v) {
        Context ctx = getActivity();
        Intent intent = new Intent(ctx, BrightcovePlayerActivity.class);
        this.startActivity(intent);
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
}
