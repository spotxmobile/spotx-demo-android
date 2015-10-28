// The MIT License (MIT)

// Copyright (c) 2015 SpotXchange, Inc.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package com.spotxchange.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.android.SpotxAdView;

/**
 * Presents a single interstitial ad.
 */
public class SpotXInterstitialActivity extends Activity implements SpotxAdListener {

    public static final String EXTRA_CHANNEL_ID = "channel_id";

    private SpotxAdView _ad = null;
    private boolean _loaded = false;
    private ProgressDialog _spinner = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotxAdSettings settings = new SpotxAdSettings(getChannelId(), "com.spotxchange.demo");
        settings.setAutoPlay(true);

        _ad = new SpotxAdView(this, settings);
        _ad.setVisibility(View.INVISIBLE);
        _ad.setAdListener(this);

        setContentView(_ad);

        _ad.init();
    }

    private int getChannelId() {
        int channelId = 85394;
        Intent intent = getIntent();
        if (intent != null) {
            channelId = intent.getIntExtra(EXTRA_CHANNEL_ID, channelId);
        }
        return channelId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!_loaded) {
            showSpinner();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSpinner();
    }

    @Override
    public void adLoaded() {
        _loaded = true;
        _ad.setVisibility(View.VISIBLE);
        hideSpinner();
    }

    private void showSpinner() {
        if (_spinner == null) {
            _spinner = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
        }
        _spinner.show();
    }

    private void hideSpinner() {
        if (_spinner != null) {
            _spinner.dismiss();
            _spinner = null;
        }
    }

    // MARK: - SpotXAdListener

    @Override
    public void adStarted() {}

    @Override
    public void adCompleted() { finish(); }

    @Override
    public void adError() { finish(); }

    @Override
    public void adExpired() { finish(); }

    @Override
    public void adClicked() {}
}
