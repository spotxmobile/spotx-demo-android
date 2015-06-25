package com.spotxchange.demo;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdView;


public class AdViewFragment extends Fragment {

    private final static String LOGTAG = "XmlFragment";

    private RelativeLayout _layout;
    private SpotxAdView _adView;

    public AdViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        _layout = (RelativeLayout)inflater.inflate(R.layout.fragment_adview, container, false);

        //((EditText)_layout.findViewById(R.id.edittext_xml_channel_id)).setHint(MainActivity.DEFAULT_CHANNEL_ID_HINT);

        // Get the channel id from the edit text
        int channelId = getChannelIdFromEditText();

        // Create the SpotX Ad
        _adView = (SpotxAdView) _layout.findViewById(R.id.adview_example);

        // Register button for channel id editing
        /*
        _layout.findViewById(R.id.button_change_xml_channel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int channelId = getChannelIdFromEditText();
                    _adView.loadAdWithNewId(channelId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });*/

        SpotxAdListener adListener = new SpotxAdListener() {
            @Override
            public void adLoaded() {
                Log.d(LOGTAG, "Ad Was loaded.");
                _adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void adStarted() {
                Log.d(LOGTAG, "Ad Was started.");
            }

            @Override
            public void adCompleted() {
                Log.d(LOGTAG, "Ad has completed.");
                _adView.setVisibility(View.GONE);
            }

            @Override
            public void adError() {
                Log.d(LOGTAG, "There was an ad error");
                _adView.setVisibility(View.GONE);
            }

            @Override
            public void adExpired() {
                Log.d(LOGTAG, "Ad has expired");
                //TODO: AdViewFragment.createNewView();
            }
        };

        _adView.setAdListener(adListener);

        return _layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _adView = null;
    }

    public int getChannelIdFromEditText() {
        return 68801;
        /*
        int result = -1;
        try {
            EditText textBox = ((EditText) _layout.findViewById(R.id.edittext_xml_channel_id));
            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
            result = Integer.parseInt(channelIdString);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return result;*/
    }
}
