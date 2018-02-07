package com.spotxchange.simple;

/**
 * NOTE: This class is used to demo Inline ad placements.
 * At the end of this class ("InlineActivity Helper Methods" MARK),
 * helper methods have been included to demonstrate
 * features like dynamic-ad resizing. These features are not required,
 * and are included only for demonstration purposes.
 */

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAd;
import com.spotxchange.v3.SpotXAdBuilder;
import com.spotxchange.v3.SpotXAdGroup;
import com.spotxchange.v3.view.InlinePresentationController;

import java.util.concurrent.Future;

public class InlineActivity extends Activity implements SpotXAdGroup.Observer {
    private static final String TAG = com.spotxchange.simple.InlineActivity.class.getSimpleName();

    private FrameLayout _frameLayout;
    private SeekBar _resizeSeekBar;
    private Point _baseSize;
    private String _channelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkinline);
        Intent intent = getIntent();
        _channelID = intent.getStringExtra(MainActivity.PARCEL_CHANNEL_ID);
        _frameLayout = (FrameLayout)findViewById(R.id.adContainer);

        // NOTE: The resize bar is not required - it is included here to demonstrate ad resizing
        _resizeSeekBar = (SeekBar)findViewById(R.id.resizeSeekBar);
        _resizeSeekBar.setEnabled(false);
        _resizeSeekBar.setOnSeekBarChangeListener(_resizeSeekBarChangeListener);

        // init SpotX
        SpotX.initialize(getApplicationContext());

        playAd();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void playAd() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // create builder
                SpotXAdBuilder adBuilder = SpotX.newAdBuilder(_channelID);
                adBuilder.useHTTPS = true;

                // specify any parameters
                adBuilder.param("custom-key", "custom-value");

                // NOTE: when no params are passed to load(), the VAST 2.0 endpoint (without podding) is used
                Future<SpotXAdGroup> future = adBuilder.load();

                try {
                    final SpotXAdGroup adGroup = future.get();
                    if(adGroup != null) {
                        adGroup.registerObserver(com.spotxchange.simple.InlineActivity.this);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                InlinePresentationController controller = new InlinePresentationController(adGroup, _frameLayout);
                                controller.play();
                            }
                        });
                    }
                    else{
                        makeToast("Empty Ad Group!");
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // MARK: SpotX Observer

    @Override
    public void onGroupStart() {
        makeToast("AdGroup Started");
        _resizeSeekBar.setEnabled(true);
        Log.d(TAG, "[Group] Start");
    }

    @Override
    public void onStart(SpotXAd ad) {
        Log.d(TAG, "[Ad] Start");
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
    public void onError(SpotXAd ad, Error error) {
        makeToast("Ad - Error");
        Log.d(TAG, "[Ad] Error!");
    }

    @Override
    public void onGroupComplete() {
        makeToast("AdGroup Complete");
        _resizeSeekBar.setEnabled(false);

        // clear the SpotXViews
        _frameLayout.removeAllViews();
        animateAdContainerClose();
        Log.d(TAG, "[Group] Complete");
    }

    @Override
    public void onTimeUpdate(SpotXAd ad, int elapsed) {

    }

    @Override
    public void onClick(SpotXAd ad) {

    }

    // MARK: InlineActivity Helper Methods

    /**
     * Helper method to show how to collapse the ad container on completion (or close)
     * NOTE: This functionality is not required
     */
    private void animateAdContainerClose() {
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(_frameLayout.getHeight(), 0);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                _frameLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                _frameLayout.requestLayout();
            }
        });
        valueAnimator.start();
    }

    /**
     * Helper method for showing toast messages
     * NOTE: This functionality is not required
     * @param message
     */
    private void makeToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(com.spotxchange.simple.InlineActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This listener demonstrates how to dynamically resize ads during playback
     * NOTE: This functionality is not required
     */
    private SeekBar.OnSeekBarChangeListener _resizeSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (null == _baseSize) {
                _baseSize = new Point(_frameLayout.getWidth(), _frameLayout.getHeight());
            }

            ViewGroup.LayoutParams params = _frameLayout.getLayoutParams();
            params.height = (int)(_baseSize.y * ((double)i / 100));
            params.width = (int)(_baseSize.x * ((double)i / 100));
            _frameLayout.setLayoutParams(params);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
