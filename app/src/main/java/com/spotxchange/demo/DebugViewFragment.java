package com.spotxchange.demo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spotxchange.demo.components.debugLog.DebugLogFragment;
import com.spotxchange.sdk.android.SpotxAdSettings;


public class DebugViewFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = DebugViewFragment.class.getSimpleName();
    public static final String QA_COOKIE_URL = "http://qa.testing.spotxchange.com/cookie/";

    private RelativeLayout _layout;
    private TextView _versionLabel;
    private WebView _cookieWebView;
    private View _sdkConfigView;

    public DebugViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = (RelativeLayout)inflater.inflate(R.layout.fragment_debug, container, false);
        setupViewButtons();
        return _layout;
    }

    private void setupViewButtons() {
        _versionLabel = (TextView) _layout.findViewById(R.id.sdk_version_label);
        _versionLabel.setText(this.getSDKVersion());

        _cookieWebView = (WebView) _layout.findViewById(R.id.webview_cookies);
        _sdkConfigView = _layout.findViewById(R.id.view_config);

        _layout.findViewById(R.id.button_cookies).setOnClickListener(this);
        _layout.findViewById(R.id.button_log).setOnClickListener(this);
        _layout.findViewById(R.id.button_config).setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadCookieWebView() {
        _cookieWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        WebSettings webSettings = _cookieWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        _cookieWebView.loadUrl(QA_COOKIE_URL);
        _cookieWebView.setVisibility(View.VISIBLE);
    }

    private String getSDKVersion() {
        return getActivity().getString(R.string.sdk_version_string, SpotxAdSettings.getSdkVersion());
    }

    private void loadConfigView() {
        _sdkConfigView.setVisibility(View.VISIBLE);
    }

    private void loadDebugLogView() {
        DebugLogFragment fragment = new DebugLogFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_container, fragment);
        fragmentTransaction.addToBackStack(this.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_cookies:
                loadCookieWebView();
                break;
            case R.id.button_log:
                loadDebugLogView();
                break;
            case R.id.button_config:
                loadConfigView();
                break;
        }
    }
}
