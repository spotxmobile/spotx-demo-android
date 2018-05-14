package com.spotxchange.testapp;

import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.spotxchange.v4.SpotXAdPlayer;
import com.spotxchange.v4.SpotXAdRequest;
import com.spotxchange.v4.SpotXResizableAdPlayer;
import com.spotxchange.v4.datamodel.SpotXAd;
import com.spotxchange.v4.datamodel.SpotXAdGroup;

public class SDKResizableActivity extends AppCompatActivity implements SpotXAdPlayer.Observer {
    private static final String TAG = SDKResizableActivity.class.getSimpleName();
    private FrameLayout _adContainer;
    private SeekBar _resizeSeekBar;
    private Point _baseSize;

    private Settings _settings;

    private SpotXResizableAdPlayer _player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkresizable);

        _settings = new Settings(this);

        _adContainer = (FrameLayout)findViewById(R.id.adContainer);

        _resizeSeekBar = (SeekBar)findViewById(R.id.resizeSeekBar);
        _resizeSeekBar.setOnSeekBarChangeListener(_resizeSeekBarChangeListener);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        playAd();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // create presenter
                _player = new SpotXResizableAdPlayer(_adContainer);
                _player.registerObserver(SDKResizableActivity.this);
                _player.load();
            }
        });
    }

    private void animateAdContainerClose() {
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(_adContainer.getHeight(), 0);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                _adContainer.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                _adContainer.requestLayout();
            }
        });
        valueAnimator.start();
    }

    private void makeToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKResizableActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener _resizeSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            _player.setSize(i / 100.f);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SDKResizableActivity.this, "Empty Ad Group!", Toast.LENGTH_LONG).show();
                    _adContainer.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onGroupStart(SpotXAdGroup group) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKResizableActivity.this, "Starting group...", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SDKResizableActivity.this, "Ad - Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "[Ad] Error!");
                _adContainer.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onGroupComplete(SpotXAdGroup group) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKResizableActivity.this, "Group completed.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "[Group] Complete");
                _adContainer.setVisibility(View.INVISIBLE);
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
