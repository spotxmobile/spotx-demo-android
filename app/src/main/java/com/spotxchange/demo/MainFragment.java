package com.spotxchange.demo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spotxchange.sdk.android.SpotxAdSettings;


public class MainFragment extends Fragment {
    private final String TAG = MainFragment.class.getSimpleName();

    Button mInterstitialActivityButton;
    Button mProgrammticButton;
    Button mXmlButton;
    Button mInterstitialViewButton;
    Button mCookiesButton;
    TextView mVersionLabel;
    WebView mCookieWebView;

    RelativeLayout mMainLayout;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);
        getReferenceToResources();
        return mMainLayout;
    }

    private void getReferenceToResources() {
        mVersionLabel = (TextView) mMainLayout.findViewById(R.id.sdk_version_label);
        mVersionLabel.setText(this.getSDKVersion());

        mCookieWebView = (WebView) mMainLayout.findViewById(R.id.webview_cookies);

        ((Button) mMainLayout.findViewById(R.id.button_adview_example))
                .setOnClickListener((MainActivity)getActivity());

        ((Button) mMainLayout.findViewById(R.id.button_cookies))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadCookieWebView();
                    }
                });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadCookieWebView() {
        mCookieWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        WebSettings webSettings = mCookieWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mCookieWebView.loadUrl("http://qa.testing.spotxchange.com/cookie/");
        mCookieWebView.setVisibility(View.VISIBLE);
    }

    private String getSDKVersion() {
        final Context ctx = getActivity();
        final String version = ctx.getString(R.string.sdk_version_string, SpotxAdSettings.getSdkVersion());
        return version;
    }
}
