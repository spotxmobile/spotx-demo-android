package com.spotxchange.demo;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.spotxchange.sdk.android.SpotxAdView;


public class AdViewFragment extends Fragment {

    private final static String LOGTAG = "XmlFragment";

    private RelativeLayout mLayoutMain;
    private SpotxAdView mSpotXView;

    /*
    VpaidEventListener mVpaidEventListener = new VpaidEventListener() {
        @Override
        public void onVpaidEvent(VpaidEvent vpaidEvent) {
            Log.d(LOGTAG, "onVpaidEvent: " + vpaidEvent);
        }
    };*/

    public AdViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mLayoutMain = (RelativeLayout)inflater.inflate(R.layout.fragment_adview, container, false);

        //((EditText)mLayoutMain.findViewById(R.id.edittext_xml_channel_id)).setHint(MainActivity.DEFAULT_CHANNEL_ID_HINT);

        // Get the channel id from the edit text
        int channelId = getChannelIdFromEditText();

        // Create the SpotX Ad
        mSpotXView = (SpotxAdView)mLayoutMain.findViewById(R.id.adview_example);

        // Listen for VPAID 2.0 Events
        //mSpotXView.setVpaidEventListener(mVpaidEventListener);

        // Register button for channel id editing
        /*
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
        });*/

        return mLayoutMain;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSpotXView = null;
    }

    public int getChannelIdFromEditText() {
        return 68801;
        /*
        int result = -1;
        try {
            EditText textBox = ((EditText) mLayoutMain.findViewById(R.id.edittext_xml_channel_id));
            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
            result = Integer.parseInt(channelIdString);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return result;*/
    }
}
