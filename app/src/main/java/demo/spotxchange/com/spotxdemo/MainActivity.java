package demo.spotxchange.com.spotxdemo;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAd;
import com.spotxchange.v3.SpotXAdGroup;
import com.spotxchange.v3.view.InterstitialPresentationController;

public class MainActivity extends AppCompatActivity implements AdLoader.Callback, SpotXAdGroup.Observer, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText _editTextChannelId;
    private ProgressBar _progressBar;
    private Button _buttonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpotX.initialize(this);

        _editTextChannelId = (EditText) findViewById(R.id.editTextChannelId);
        _progressBar = (ProgressBar) findViewById(R.id.progressBar);
        _buttonPlay = (Button) findViewById(R.id.buttonPlayAd);
        _buttonPlay.setOnClickListener(this);

        showLoadingIndicator(false);
    }


    // MARK: View OnClickListener

    @Override
    public void onClick(View view) {
        loadAd();
    }

    private void loadAd() {
        String channel = _editTextChannelId.getText().toString();
        if (!TextUtils.isEmpty(channel)) {
            AdLoader loader = new AdLoader(channel, 1, 10 /*seconds*/, this);
            loader.execute();
        }
    }


    // MARK: AdLoader.Callback

    @Override
    public void adLoadingStarted() {
        showLoadingIndicator(true);
    }

    @Override
    public void adLoadingFinished(@Nullable SpotXAdGroup adGroup) {
        showLoadingIndicator(false);
        if(adGroup == null) {
            showNoAdsMessage();
        }
        else{
            showAd(adGroup);
        }
    }

    private void showLoadingIndicator(final boolean visible) {
        _progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        _buttonPlay.setEnabled(!visible);
    }

    private void showNoAdsMessage() {
        Log.d(TAG, "No Ad Found");
        Toast.makeText(MainActivity.this, "No Ad Found", Toast.LENGTH_LONG).show();
    }

    private void showAd(final SpotXAdGroup group) {
        InterstitialPresentationController.show(MainActivity.this, group);
    }


    // MARK: SpotX Observer

    @Override
    public void onGroupStart() {
        Log.d(TAG, "Group Start");
    }

    @Override
    public void onStart(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Start");
    }

    @Override
    public void onComplete(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Complete");
    }

    @Override
    public void onSkip(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Skip");
    }

    @Override
    public void onError(SpotXAd spotXAd, Error error) {
        Log.d(TAG, "Ad Error: " + error.getLocalizedMessage());
    }

    @Override
    public void onGroupComplete() {
        Log.d(TAG, "Group Complete");
    }

    @Override
    public void onTimeUpdate(SpotXAd spotXAd, int i) {
        // do nothing
    }

    @Override
    public void onClick(SpotXAd spotXAd) {
        Log.d(TAG, "Ad Clicked");
    }

}
