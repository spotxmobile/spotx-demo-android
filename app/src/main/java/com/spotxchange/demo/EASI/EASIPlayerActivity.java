package com.spotxchange.demo.EASI;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;


import com.spotxchange.demo.R;

public class EASIPlayerActivity extends Activity {

    private WebView _webView;

    private String _easiDocument = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easiplayer);

        Intent intent = getIntent();
        _easiDocument = intent.getStringExtra(EASIMainActivity.EASI_DOCUMENT);

        _webView = (WebView) findViewById(R.id.webView);
        WebView.setWebContentsDebuggingEnabled(true);
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        _webView.addJavascriptInterface(this, "spotxDemo");

        _webView.loadDataWithBaseURL(
                "http://search.spotxchange.com",
                _easiDocument,
                "text/html",
                "utf8",
                null
        );

    }

    @Override
    protected void onPause () {
        super.onPause();
        if (null != _webView) {
            /*
                From: http://developer.android.com/reference/android/webkit/WebView.html#onPause()

                Pauses any extra processing associated with this WebView and its associated DOM,
                plugins, JavaScript etc. For example, if this WebView is taken offscreen,
                this could be called to reduce unnecessary CPU or network traffic.
                When this WebView is again "active", call onResume().
             */
            _webView.onPause();
            _webView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != _webView) {
            /*
                Resumes a WebView after a previous call to onPause().
             */
            _webView.onResume();
            _webView.resumeTimers();
        }
    }

    @JavascriptInterface
    public void onEASIComplete() {
        //this.finish();
    }

}
