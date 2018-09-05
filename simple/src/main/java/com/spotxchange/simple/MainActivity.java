package com.spotxchange.simple;

/**
 * NOTE: This class is used to demo Interstitial ad placements.
 * This class also contains logic for triggering an Inline ad placement,
 * however, it doesn't demo Inline placements.
 * For an Inline integration demo, look at InlineActivity.java
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAd;
import com.spotxchange.v3.SpotXAdGroup;
import com.spotxchange.v3.view.InterstitialPresentationController;

public class MainActivity extends AppCompatActivity implements AdLoader.Callback, SpotXAdGroup.Observer, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText _editTextChannelId;
    private ProgressBar _progressBar;
    private Button _buttonPlayInterstitial;
    private Button _buttonPlayInline;

    public final static String PARCEL_CHANNEL_ID = "channelID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View contentView = (View) findViewById(android.R.id.content);

        contentView.getViewTreeObserver().addOnGlobalFocusChangeListener(new android.view.ViewTreeObserver.OnGlobalFocusChangeListener() {
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                int i = 0;
                i++;
                if (newFocus != null) {
                    newFocus.setBackgroundColor(Color.BLUE);
                }
            }
        });

        View focusedView = contentView.findFocus();
        if (focusedView != null) {
            // Shouldn't have any
            return;
        }

        SpotX.initialize(this);

        _editTextChannelId = (EditText) findViewById(R.id.editTextChannelId);
        _progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Interstitial
        _buttonPlayInterstitial = (Button) findViewById(R.id.buttonPlayAd);
        _buttonPlayInterstitial.setOnClickListener(this);

        // Inline
        _buttonPlayInline = (Button) findViewById(R.id.buttonPlayAdInline);
        _buttonPlayInline.setOnClickListener(this);

        showLoadingIndicator(false);
    }


    // MARK: View OnClickListener

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPlayAd:
                loadAdInterstitial();
                break;
            case R.id.buttonPlayAdInline:
                loadAdInline(view);
                break;
        }
    }

    private void loadAdInterstitial() {
        String channel = _editTextChannelId.getText().toString();
        if (!TextUtils.isEmpty(channel)) {
            AdLoader loader = new AdLoader(channel, 1, 10 /*seconds*/, this);
            loader.execute();
        }
    }

    private void loadAdInline(View view) {
        Intent intent = new Intent(view.getContext(), com.spotxchange.simple.InlineActivity.class);
        intent.putExtra(PARCEL_CHANNEL_ID, _editTextChannelId.getText().toString());
        intent.putExtra("count", "1");
        startActivity(intent);
    }


    // MARK: AdLoader.Callback

    @Override
    public void adLoadingStarted() {
        showLoadingIndicator(true);
    }

    @Override
    public void adLoadingFinished(@Nullable SpotXAdGroup adGroup) {
        showLoadingIndicator(false);
        if(adGroup == null) {
            showNoAdsMessage();
        }
        else{
            adGroup.focusable = false;
            showAd(adGroup);
        }
    }

    private void showLoadingIndicator(final boolean visible) {
        _progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        _buttonPlayInterstitial.setEnabled(!visible);
        _buttonPlayInline.setEnabled(!visible);
    }

    private void showNoAdsMessage() {
        Log.d(TAG, "No Ad Found");
        Toast.makeText(MainActivity.this, "No Ad Found", Toast.LENGTH_LONG).show();
    }

    private void showAd(final SpotXAdGroup group) {
        InterstitialPresentationController.show(MainActivity.this, group);
    }


    // MARK: SpotX Observer

    @Override
    public void onGroupStart() {
        Log.d(TAG, "Group Start");
    }

    @Override
    public void onStart(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Start");
    }

    @Override
    public void onComplete(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Complete");
    }

    @Override
    public void onSkip(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Skip");
    }

    @Override
    public void onError(SpotXAd spotXAd, Error error) {
        Log.d(TAG, "Ad Error: " + error.getLocalizedMessage());
    }

    @Override
    public void onGroupComplete() {
        Log.d(TAG, "Group Complete");
    }

    @Override
    public void onTimeUpdate(SpotXAd spotXAd, int i) {
        // do nothing
    }

    @Override
    public void onClick(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Clicked");
    }

}
