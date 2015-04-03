package com.spotxchange.demo;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.spotxchange.integration.mraid.SpotXProperties;
import com.spotxchange.integration.mraid.SpotXView;
import com.spotxchange.integration.mraid.enumerations.VpaidEvent;
import com.spotxchange.integration.mraid.utils.VpaidEventListener;

public class ProgrammaticFragment extends Fragment implements View.OnClickListener {

    private static final int AD_HEIGHT_PIXELS = 500;

    private final static String LOGTAG = "ProgrammaticFragment";

    RelativeLayout mLayoutMain;
    SpotXView mSpotXView;

    VpaidEventListener mVpaidEventListener = new VpaidEventListener() {
        @Override
        public void onVpaidEvent(VpaidEvent vpaidEvent) {
            Log.d(LOGTAG, "onVpaidEvent: " + vpaidEvent);
        }
    };

    public ProgrammaticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mLayoutMain = (RelativeLayout)inflater.inflate(R.layout.fragment_programmatic, container, false);

        ((EditText)mLayoutMain.findViewById(R.id.edittext_programmatic_channel_id)).setHint(MainActivity.DEFAULT_CHANNEL_ID_HINT);

        // Get the Channel Id
        int channelId = getChannelIdFromEditText();

        // Create an INLINE SpotX Ad
        mSpotXView = new SpotXView(getActivity());
        SpotXProperties properties = new SpotXProperties();
        properties.put(SpotXProperties.APP_STORE_URL,
                "https://play.google.com/store/apps/details?id=com.example.app");
        properties.put(SpotXProperties.APP_IAB_CATEGORY, "IAB1");
        properties.setUseInternalBrowser(true);
        // R.id.programmatic_ad set in the ids.xml values file
        mSpotXView.initialize(R.id.programmatic_ad, channelId, "com.app.publisher.domain", properties);

        // Listen for VPAID 2.0 Events
        mSpotXView.setVpaidEventListener(mVpaidEventListener);

        // Relative layout params
        RelativeLayout.LayoutParams adLayoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AD_HEIGHT_PIXELS);
        adLayoutParams.addRule(RelativeLayout.BELOW, R.id.button_change_programmatic_channel);

        // Add the Ad to the main layout
        mLayoutMain.addView(mSpotXView, adLayoutParams);

        // Register button for channel id editing
        mLayoutMain.findViewById(R.id.button_change_programmatic_channel).setOnClickListener(this);

        return mLayoutMain;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_change_programmatic_channel) {
            int channelId = getChannelIdFromEditText();
            mSpotXView.loadAdWithNewId(channelId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSpotXView.close();
    }

    public int getChannelIdFromEditText() {
        int result = -1;
        try {
            EditText textBox = ((EditText) mLayoutMain.findViewById(R.id.edittext_programmatic_channel_id));
            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
            result = Integer.parseInt(channelIdString);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return result;
    }
}
