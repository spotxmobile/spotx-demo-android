package com.spotxchange.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.spotxchange.integration.mraid.SpotXProperties;
import com.spotxchange.integration.mraid.SpotXView;
import com.spotxchange.integration.mraid.enumerations.PlacementType;
import com.spotxchange.integration.mraid.enumerations.VpaidEvent;
import com.spotxchange.integration.mraid.utils.VpaidEventListener;


public class InterstitialViewFragment extends Fragment implements View.OnClickListener {

    private static final String LOGTAG = InterstitialViewFragment.class.getSimpleName();

    RelativeLayout mLayoutMain;
    SpotXView mSpotXView;

    VpaidEventListener mVpaidEventListener = new VpaidEventListener() {
        @Override
        public void onVpaidEvent(VpaidEvent vpaidEvent) {
            Log.d(LOGTAG, "onVpaidEvent: " + vpaidEvent);

            switch (vpaidEvent) {
                case AD_VIDEO_COMPLETE:
                case AD_ERROR:
                    destroyInterstitialView();
            }
        }
    };

    public InterstitialViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayoutMain = (RelativeLayout)inflater.inflate(R.layout.fragment_interstitial_view, container, false);

        mLayoutMain.findViewById(R.id.button_launch_interstitial_view).setOnClickListener(this);
        ((EditText)mLayoutMain.findViewById(R.id.edittext_launch_interstitial_view_channel_id)).setHint(MainActivity.DEFAULT_CHANNEL_ID_HINT);

        return mLayoutMain;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_launch_interstitial_view) {
            launchInterstitialView();
        }
    }

    @Override
    public void onDestroyView() {
        destroyInterstitialView();
        super.onDestroyView();
    }

    public int getChannelIdFromEditText() {
        int result = -1;
        try {
            EditText textBox = ((EditText) mLayoutMain.findViewById(R.id.edittext_launch_interstitial_view_channel_id));
            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
            result = Integer.parseInt(channelIdString);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return result;
    }

    private void launchInterstitialView() {
        destroyInterstitialView();

        int channelId = getChannelIdFromEditText();

        // Create an INTERSTITIAL SpotX Ad

        SpotXProperties properties = new SpotXProperties();
        properties.put(SpotXProperties.APP_IAB_CATEGORY, "IAB1");
        properties.put(SpotXProperties.APP_STORE_URL, "https://play.google.com/store/apps/details?id=com.example.app");

        // R.id.programmatic_ad set in the ids.xml values file
        mSpotXView = new SpotXView(getActivity(), PlacementType.INTERSTITIAL);
        mSpotXView.initialize(R.id.programmatic_ad, channelId, "com.app.publisher.domain", properties);
        mSpotXView.setVpaidEventListener(mVpaidEventListener);

        // Relative layout params
        RelativeLayout.LayoutParams adLayoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        getRootContentView().addView(mSpotXView);
    }

    private void destroyInterstitialView() {
        if (mSpotXView != null) {
            getRootContentView().removeView(mSpotXView);
            mSpotXView.close();
            mSpotXView = null;
        }
    }

    private ViewGroup getRootContentView() {
        return (ViewGroup) getView().getRootView().findViewById(android.R.id.content);
    }
}
