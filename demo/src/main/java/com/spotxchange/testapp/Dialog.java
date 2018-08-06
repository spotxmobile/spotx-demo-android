package com.spotxchange.testapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.spotxchange.v4.SpotXInlineAdPlayer;
import com.spotxchange.v4.SpotXAdPlayer;
import com.spotxchange.v4.SpotXAdRequest;
import com.spotxchange.v4.datamodel.SpotXAd;
import com.spotxchange.v4.datamodel.SpotXAdGroup;


public class Dialog extends AlertDialog implements SpotXAdPlayer.Observer {

    private FrameLayout _adContainer;
    private Settings _settings;

    protected Dialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sdk);
        _settings = new Settings(getContext());
        _adContainer = (FrameLayout)findViewById(R.id.adContainer);
    }

    @Override
    public void show() {
        super.show();

        SpotXInlineAdPlayer player = new SpotXInlineAdPlayer(_adContainer);
        player.registerObserver(this);
        player.load();
    }


    // SpotXAdPlayer Observer

    @Override
    public SpotXAdRequest requestForPlayer(@NonNull SpotXAdPlayer player) {
        return _settings.requestWithSettings();
    }

    @Override
    public void onLoadedAds(SpotXAdPlayer player, SpotXAdGroup group, Exception exception) {
        if (group != null && group.ads.size() > 0) {
            player.start();
        }
    }

    @Override
    public void onGroupStart(SpotXAdGroup group) {

    }

    @Override
    public void onStart(SpotXAd ad) {

    }

    @Override
    public void onPlay(SpotXAd ad) {

    }

    @Override
    public void onPause(SpotXAd ad) {

    }

    @Override
    public void onTimeUpdate(SpotXAd ad, double elapsed) {

    }

    @Override
    public void onClick(SpotXAd ad) {

    }

    @Override
    public void onComplete(SpotXAd ad) {

    }

    @Override
    public void onSkip(SpotXAd ad) {

    }

    @Override
    public void onUserClose(SpotXAd ad) {

    }

    @Override
    public void onError(SpotXAd ad, Exception exception) {

    }

    @Override
    public void onGroupComplete(SpotXAdGroup group) {

    }
}
