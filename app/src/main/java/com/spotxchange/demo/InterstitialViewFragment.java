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

    private static final String LOGTAG = "InterstitlViewFragment";

    RelativeLayout mLayoutMain;
    SpotXView mSpotXView;

    VpaidEventListener mVpaidEventListener = new VpaidEventListener() {
        @Override
        public void onVpaidEvent(VpaidEvent vpaidEvent) {
            Log.d(LOGTAG, "onVpaidEvent: " + vpaidEvent);
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
        super.onDestroyView();
        if (mSpotXView != null) {
            mSpotXView.close();
        }
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

    public void launchInterstitialView() {
        int channelId = getChannelIdFromEditText();

        // Create an INTERSTITIAL SpotX Ad
        mSpotXView = new SpotXView(getActivity(), PlacementType.INTERSTITIAL);
        SpotXProperties properties = new SpotXProperties();
        properties.put(SpotXProperties.APP_STORE_URL,
                "https://play.google.com/store/apps/details?id=com.example.app");
        properties.put(SpotXProperties.APP_IAB_CATEGORY, "IAB1");
        // R.id.programmatic_ad set in the ids.xml values file
        mSpotXView.initialize(R.id.programmatic_ad, channelId, "com.app.publisher.domain", properties);

        // Listen for VPAID 2.0 Events
        mSpotXView.setVpaidEventListener(mVpaidEventListener);

        // Relative layout params
        RelativeLayout.LayoutParams adLayoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Try/Catch because it's not uncommon to create fragments without views.
        try {
            ((FrameLayout)(getView().getRootView().findViewById(android.R.id.content))).addView(mSpotXView, adLayoutParams);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}
