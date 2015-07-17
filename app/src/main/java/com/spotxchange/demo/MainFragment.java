package com.spotxchange.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;


public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    RelativeLayout _layout;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _layout = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);
        getReferenceToResources();
        return _layout;
    }

    private void getReferenceToResources() {
        ((Button) _layout.findViewById(R.id.button_adview_example))
                .setOnClickListener((MainActivity)getActivity());
        ((Button) _layout.findViewById(R.id.button_debug))
                .setOnClickListener((MainActivity)getActivity());
    }
}
