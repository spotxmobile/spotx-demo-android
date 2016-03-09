package com.spotxchange.demo.mopub;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
//import android.support.v7.app.AppCompatActivity;


import com.spotxchange.demo.R;

public class MopubMainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_main);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabSpec tab1 = tabHost.newTabSpec("Interstitial");
        TabSpec tab2 = tabHost.newTabSpec("Rewarded Video");

        tab1.setIndicator("MoPub: Interstitial");
        tab1.setContent(new Intent(this, MopubInterstitial.class));

        tab2.setIndicator("MoPub: Rewarded Video");
        tab2.setContent(new Intent(this, MopubRewardedVideo.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }

}

