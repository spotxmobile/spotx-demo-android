package com.spotxchange.demo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spotxchange.sdk.android.SpotxAdSettings;


public class MainFragment extends Fragment {
    private final static String LOGTAG = MainFragment.class.getSimpleName();

    TextView _versionLabel;
    WebView _cookieWebView;

    RelativeLayout _mainLayout;

    public MainFragment() {} // Required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _mainLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);
        getReferenceToResources();
        return _mainLayout;
    }

    private void getReferenceToResources() {
        _versionLabel = (TextView) _mainLayout.findViewById(R.id.sdk_version_label);
        _versionLabel.setText(this.getSDKVersion());
        _cookieWebView = (WebView) _mainLayout.findViewById(R.id.webview_cookies);
        _mainLayout.findViewById(R.id.button_adview_example).setOnClickListener((MainActivity)getActivity());
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

        _cookieWebView.getSettings().setJavaScriptEnabled(true);
        _cookieWebView.loadUrl(getString(R.string.qa_cookie_url));
        _cookieWebView.setVisibility(View.VISIBLE);
    }

    private String getSDKVersion() {
        final String version = getString(R.string.sdk_version_string, SpotxAdSettings.getSdkVersion());
        return version;
    }
}
