package com.spotxchange.demo;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.android.SpotxAdView;


public class AdViewFragment extends Fragment implements View.OnClickListener{

    public final static String TAG = AdViewFragment.class.getSimpleName();

    private RelativeLayout _layout;
    private static SpotxAdView _adView;
    private SpotxAdListener _adListener;

    public AdViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = (RelativeLayout)inflater.inflate(R.layout.fragment_adview, container, false);

        ((EditText) _layout.findViewById(R.id.edittext_launch_adview_channel_id)).setHint(
                getActivity().getString(R.string.default_channel));

        _adListener = new SpotxAdListener() {
            @Override
            public void adLoaded() {
                Log.d(TAG, "Ad was loaded.");
                _layout.findViewById(R.id.button_launch_adview).setVisibility(View.VISIBLE);
                setLaunchButtonLoaded();
            }

            @Override
            public void adStarted() {
                Log.d(TAG, "Ad was started.");
                _layout.findViewById(R.id.button_launch_adview).setVisibility(View.INVISIBLE);
            }

            @Override
            public void adCompleted() {
                Log.d(TAG, "Ad has completed.");
                if (_adView != null) {
                    _adView.unsetAdListener();
                    _layout.removeView(_adView);
                    _adView = null;
                }
            }

            @Override
            public void adError() {
                Log.d(TAG, "Ad failed with error");
                if (_adView != null) {
                    _adView.unsetAdListener();
                    _layout.removeView(_adView);
                    _adView = null;
                }
                Toast.makeText(getActivity(), "Ad failed with error.", Toast.LENGTH_SHORT).show();
                setLaunchButtonInvisible();
            }

            @Override
            public void adExpired() {
                Log.d(TAG, "Ad has expired");
                if (_adView != null) {
                    _adView.unsetAdListener();
                    _layout.removeView(_adView);
                    _adView = null;
                }
                Toast.makeText(getActivity(), "Ad expired. Ad has been purged.", Toast.LENGTH_SHORT).show();
                setLaunchButtonInvisible();
            }

            @Override
            public void adClicked() {
                Log.d(TAG, "Ad Clicked");
            }
        };

        _layout.findViewById(R.id.button_load_adview).setOnClickListener(this);
        _layout.findViewById(R.id.button_launch_adview).setOnClickListener(this);
        return _layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _adView = null;
    }

    public int getChannelIdFromEditText() {
        int result = -1;
        try {
            EditText textBox = ((EditText) _layout.findViewById(R.id.edittext_launch_adview_channel_id));
            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
            result = Integer.parseInt(channelIdString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private SpotxAdView createNewAdView(SpotxAdListener adListener) {
        String appDomain = getActivity().getString(R.string.app_domain);
        SpotxAdSettings settings = new SpotxAdSettings(getChannelIdFromEditText(), appDomain, "interstitial");
        SpotxAdView adView = (SpotxAdView) LayoutInflater.from(getActivity()).inflate(R.layout.adview, _layout, false);
        adView.setAdSettings(settings);
        adView.setVisibility(View.INVISIBLE);
        adView.setAdListener(adListener);
        adView.init();
        return adView;
    }

    private void setLaunchButtonLoading() {
        _layout.findViewById(R.id.button_launch_adview).setEnabled(false);
        ((Button) _layout.findViewById(R.id.button_launch_adview)).setTextColor(getResources().getColor(R.color.gray));
        ((Button) _layout.findViewById(R.id.button_launch_adview)).setText(R.string.loading_text);
        _layout.findViewById(R.id.button_launch_adview).setVisibility(View.VISIBLE);
    }

    private void setLaunchButtonLoaded() {
        _layout.findViewById(R.id.button_launch_adview).setEnabled(true);
        ((Button) _layout.findViewById(R.id.button_launch_adview)).setTextColor(getResources().getColor(R.color.spotxgreen));
        ((Button) _layout.findViewById(R.id.button_launch_adview)).setText(R.string.launch_text);
    }

    private void setLaunchButtonInvisible() {
        _layout.findViewById(R.id.button_launch_adview).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_load_adview:
                try {
                    Log.d(TAG, "reload button clicked");
                    setLaunchButtonLoading();
                    if (_adView != null) {
                        _adView.unsetAdListener();
                        _layout.removeView(_adView);
                        _adView = null;
                    }
                    _adView = createNewAdView(_adListener);
                    _layout.addView(_adView);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.button_launch_adview:
                Log.d(TAG, "launch button clicked");
                _adView.setVisibility(View.VISIBLE);
                setLaunchButtonInvisible();
                break;
        }
    }

    public static boolean isAdViewVisible(){
        return (_adView != null && _adView.getVisibility() == View.VISIBLE);
    }

    public static void destroyAdView(){
        if(_adView != null) {
            _adView.setVisibility(View.GONE);
            _adView = null;
        }
    }

}
