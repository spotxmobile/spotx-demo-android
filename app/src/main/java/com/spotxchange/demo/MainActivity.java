package com.spotxchange.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.spotxchange.demo.EASI.EASIMainActivity;
import com.spotxchange.demo.brightcove.BrightcoveTestActivity;
import com.spotxchange.demo.mopub.MopubMainActivity;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

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
            CrashManager.register(this, getString(R.string.HOCKEY_API_KEY));
        }
    }

    private void checkForUpdates() {
        // TODO: Remove this for Google Play Store builds!
        if (!BuildConfig.DEBUG) {
            UpdateManager.register(this, getString(R.string.HOCKEY_API_KEY));
        }
    }

    /*------------- View.OnClickListener -------------*/

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_adview_example:
                loadAdViewFragment();
                break;
            case R.id.button_debug:
                loadDebugFragment();
                break;
            case R.id.button_testsuite:
                loadTestSuiteFragment();
                break;
            case R.id.button_inline_example:
                loadInlineFragment();
                break;
            case R.id.button_brightcove:
                Intent intent = new Intent(MainActivity.this, BrightcoveTestActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.button_mopub:
                Intent mopubIntent = new Intent(MainActivity.this, MopubMainActivity.class);
                MainActivity.this.startActivity(mopubIntent);
                break;
            case R.id.button_easi:
                Intent easiIntent = new Intent(MainActivity.this, EASIMainActivity.class);
                MainActivity.this.startActivity(easiIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        WebView cookies = (WebView)findViewById(R.id.webview_cookies);
        if (cookies != null && cookies.getVisibility() == View.VISIBLE) {
            findViewById(R.id.webview_cookies).setVisibility(View.INVISIBLE);
            findViewById(R.id.dbg_btn_container).setVisibility(View.VISIBLE);
        }
        else if(AdViewFragment.isAdViewVisible()){
            AdViewFragment.destroyAdView();
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
        loadFragment(new AdViewFragment(), AdViewFragment.TAG);
    }

    private void loadInlineFragment() {
        loadFragment(AdViewFragment.newInstance(R.layout.fragment_scrollcontent), AdViewFragment.TAG);
    }

    private void loadDebugFragment() {
        loadFragment(new DebugViewFragment(), this.TAG);
    }

    private void loadTestSuiteFragment() {
        loadFragment(new TestSuiteViewFragment(), null);
    }

    private void loadFragment(Fragment f, String backstackTag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_container, f);
        fragmentTransaction.addToBackStack(backstackTag);
        fragmentTransaction.commit();
    }
}