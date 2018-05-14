package com.spotxchange.testapp;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.spotxchange.v4.SpotXAdPlayer;
import com.spotxchange.v4.SpotXAdRequest;
import com.spotxchange.v4.SpotXInlineAdPlayer;
import com.spotxchange.v4.datamodel.SpotXAd;
import com.spotxchange.v4.datamodel.SpotXAdGroup;

public class SDKInLineActivity extends AppCompatActivity implements SpotXAdPlayer.Observer {
    private static final String TAG = SDKInLineActivity.class.getSimpleName();
    private FrameLayout _adContainer;

    private Settings _settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkinline);

        _settings = new Settings(this);

        _adContainer = (FrameLayout)findViewById(R.id.adContainer);

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
                SpotXInlineAdPlayer player = new SpotXInlineAdPlayer(_adContainer);
                player.registerObserver(SDKInLineActivity.this);
                player.load(SDKInLineActivity.this);
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
                Toast.makeText(SDKInLineActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // https://stackoverflow.com/a/13381228
    private static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SDKInLineActivity.this, "Empty Ad Group!", Toast.LENGTH_LONG).show();
                    collapse(_adContainer);
                }
            });
        }
    }

    @Override
    public void onGroupStart(SpotXAdGroup group) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKInLineActivity.this, "Starting group...", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SDKInLineActivity.this, "Ad - Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "[Ad] Error!");
                collapse(_adContainer);
            }
        });
    }

    @Override
    public void onGroupComplete(SpotXAdGroup group) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SDKInLineActivity.this, "Group completed.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "[Group] Complete");
                collapse(_adContainer);
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
