package com.spotxchange.spotxadtest;

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

public class XmlFragment extends Fragment {

    private final static String LOGTAG = "XmlFragment";

    private RelativeLayout mLayoutMain;
    private SpotXView mSpotXView;

    VpaidEventListener mVpaidEventListener = new VpaidEventListener() {
        @Override
        public void onVpaidEvent(VpaidEvent vpaidEvent) {
            Log.d(LOGTAG, "onVpaidEvent: " + vpaidEvent);
        }
    };

    public XmlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mLayoutMain = (RelativeLayout)inflater.inflate(R.layout.fragment_xml, container, false);

        ((EditText)mLayoutMain.findViewById(R.id.edittext_xml_channel_id)).setHint(MainActivity.DEFAULT_CHANNEL_ID_HINT);

        // Get the channel id from the edit text
        int channelId = getChannelIdFromEditText();

        // Create the SpotX Ad
        mSpotXView = (SpotXView)mLayoutMain.findViewById(R.id.inline_spotx_ad_example);
        SpotXProperties properties = new SpotXProperties();
        properties.put(SpotXProperties.APP_STORE_URL,
                "https://play.google.com/store/apps/details?id=com.example.app");
        properties.put(SpotXProperties.APP_IAB_CATEGORY, "IAB1");
        properties.setUseInternalBrowser(true);
        mSpotXView.initialize(R.id.inline_spotx_ad_example, channelId, "com.app.publisher.domain", properties);

        // Listen for VPAID 2.0 Events
        mSpotXView.setVpaidEventListener(mVpaidEventListener);

        // Register button for channel id editing
        mLayoutMain.findViewById(R.id.button_change_xml_channel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int channelId = getChannelIdFromEditText();
                    mSpotXView.loadAdWithNewId(channelId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return mLayoutMain;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSpotXView.close();
    }

    public int getChannelIdFromEditText() {
        int result = -1;
        try {
            EditText textBox = ((EditText) mLayoutMain.findViewById(R.id.edittext_xml_channel_id));
            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
            result = Integer.parseInt(channelIdString);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return result;
    }
}
