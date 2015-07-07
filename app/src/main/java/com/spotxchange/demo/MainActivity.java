package com.spotxchange.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends Activity implements View.OnClickListener {

    // Integration = 82949
    // Stage2 = 88980
    // Production = 90782

    /*------------- Activity-------------*/

    @Override
    @TargetApi(19)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForCrashes();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (savedInstanceState == null) {
            loadMainFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUpdates();
    }

    private void checkForCrashes() {
        if (!BuildConfig.DEBUG) {
            CrashManager.register(this, getString(R.string.hockeyapp_api_key));
        }
    }

    private void checkForUpdates() {
        // TODO: Remove this for Google Play Store builds!
        if (!BuildConfig.DEBUG) {
            UpdateManager.register(this, getString(R.string.hockeyapp_api_key));
        }
    }

    /*------------- View.OnClickListener -------------*/

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_adview_example){
            loadAdViewFragment();
        }
    }

    @Override
    public void onBackPressed() {
        WebView cookies = (WebView)findViewById(R.id.webview_cookies);
        if (cookies != null && cookies.getVisibility() == View.VISIBLE) {
            findViewById(R.id.webview_cookies).setVisibility(View.INVISIBLE);
        }
        else {
            super.onBackPressed();
        }
    }

    /*------------------ Begin Custom Logic ------------------*/

    private void loadMainFragment() {
        MainFragment fragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_main_container, fragment);
        fragmentTransaction.commit();
    }

    private void loadAdViewFragment() {
        AdViewFragment fragment = new AdViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
