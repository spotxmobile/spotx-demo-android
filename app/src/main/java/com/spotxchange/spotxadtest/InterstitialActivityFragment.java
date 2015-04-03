package com.spotxchange.spotxadtest;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.spotxchange.integration.mraid.SpotXInterstitialActivity;
import com.spotxchange.integration.mraid.SpotXProperties;

public class InterstitialActivityFragment extends Fragment {

    public InterstitialActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout =  (RelativeLayout)inflater.inflate(R.layout.fragment_interstitial_activity, container, false);
        ((EditText)layout.findViewById(R.id.edittext_launch_interstitial_channel_id)).setHint(MainActivity.DEFAULT_CHANNEL_ID_HINT);

        layout.findViewById(R.id.button_launch_interstitial).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent interstitialAdIntent = new Intent(getActivity(), SpotXInterstitialActivity.class);

                        int channelId = -1;
                        try {
                            EditText textBox = ((EditText) InterstitialActivityFragment.this.getView()
                                    .findViewById(R.id.edittext_launch_interstitial_channel_id));
                            String channelIdString = (textBox.getText().toString().isEmpty() || textBox.getText() == null) ? textBox.getHint().toString() : textBox.getText().toString();
                            channelId = Integer.parseInt(channelIdString);
                        } catch (Exception ignore) {
                            ignore.printStackTrace();

                        }

                        interstitialAdIntent.putExtra(SpotXInterstitialActivity.EXTRA_CHANNEL_ID, channelId); // required
                        interstitialAdIntent.putExtra(SpotXInterstitialActivity.EXTRA_APP_PUBLISHER_DOMAIN, "www.spotxchange.com"); // required

                        SpotXProperties recommendedProperties = new SpotXProperties();
                        recommendedProperties.put(SpotXProperties.APP_IAB_CATEGORY, "IAB1");
                        recommendedProperties.put(SpotXProperties.APP_STORE_URL, "https://play.google.com/store/apps/details?id=com.example.app");

                        interstitialAdIntent.putExtra(SpotXInterstitialActivity.EXTRA_PROPERTIES, recommendedProperties); // recommended
                        startActivity(interstitialAdIntent);
                    }
                }
        );

        return layout;
    }
}
