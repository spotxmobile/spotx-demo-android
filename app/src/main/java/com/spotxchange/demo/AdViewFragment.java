package com.spotxchange.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.android.SpotxAdView;


public class AdViewFragment extends Fragment {

    private final static String LOGTAG = AdViewFragment.class.getSimpleName();

    private RelativeLayout _layout;
    private SpotxAdView _adView;
    private SpotxAdView _adView2;

    public AdViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        _layout = (RelativeLayout)inflater.inflate(R.layout.fragment_adview, container, false);

        ((EditText) _layout.findViewById(R.id.edittext_launch_adview_channel_id)).setHint(
                getActivity().getResources().getString(R.string.default_channel)
                );

        // Create the SpotX Ad
        _adView = (SpotxAdView) _layout.findViewById(R.id.adview_example);
        _adView2 = (SpotxAdView) _layout.findViewById(R.id.adview_example);

        final SpotxAdListener adListener = new SpotxAdListener() {
            @Override
            public void adLoaded() {
                Log.d(LOGTAG, "Ad was loaded.");
                _adView.setVisibility(View.VISIBLE);
                _layout.findViewById(R.id.button_launch_adview).setVisibility(View.VISIBLE);
            }

            @Override
            public void adStarted() {
                Log.d(LOGTAG, "Ad was started.");
                _layout.findViewById(R.id.button_launch_adview).setVisibility(View.INVISIBLE);

                _layout.findViewById(R.id.layout_interstitial_controls).setVisibility(View.INVISIBLE);
            }

            @Override
            public void adCompleted() {
                Log.d(LOGTAG, "Ad has completed.");
                _adView.setVisibility(View.GONE);

                _layout.findViewById(R.id.layout_interstitial_controls).setVisibility(View.VISIBLE);
            }

            @Override
            public void adError() {
                Log.d(LOGTAG, "Ad failed with error");
                _adView.setVisibility(View.GONE);

                new AlertDialog.Builder(getActivity())
                    .setTitle("Ad error.")
                    .setMessage("Ad failed with error.")
                    /*.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

                _layout.findViewById(R.id.button_launch_adview).setVisibility(View.INVISIBLE);
            }

            @Override
            public void adExpired() {
                Log.d(LOGTAG, "Ad has expired");
                //TODO: AdViewFragment.createNewView();
            }
        };


        // Register button for channel id editing
        _layout.findViewById(R.id.button_load_adview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(LOGTAG, "reload button clicked");
                    _adView.setVisibility(View.GONE);
                    _layout.findViewById(R.id.button_launch_adview).setVisibility(View.INVISIBLE);
                    _layout.removeView(_adView);
                    _adView.unsetAdListener();
                    _adView = null;

                    String appDomain = getActivity().getResources().getString(R.string.app_domain);
                    SpotxAdSettings settings = new SpotxAdSettings(getChannelIdFromEditText(), appDomain, "interstitial");

                    _adView = new SpotxAdView(getActivity(), settings);
                    _adView.setVisibility(View.INVISIBLE);
                    _adView.setAdListener(adListener);

                    _adView2 = new SpotxAdView(getActivity(), settings);
                    _adView2.setVisibility(View.INVISIBLE);
                    _adView2.setAdListener(adListener);

                    //_adView.setVisibility(View.VISIBLE);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Register button for precached ad launch
        _layout.findViewById(R.id.button_launch_adview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "launch button clicked");
                _adView.setVisibility(View.VISIBLE);
            }
        });

        _adView.setAdListener(adListener);

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
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return result;
    }
}
