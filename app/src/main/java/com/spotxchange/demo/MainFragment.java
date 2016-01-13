package com.spotxchange.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private RelativeLayout _layout;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);
        getReferenceToResources();
        return _layout;
    }

    private void getReferenceToResources() {
        /*_layout.findViewById(R.id.button_adview_example).setOnClickListener((MainActivity)getActivity());
        _layout.findViewById(R.id.button_debug).setOnClickListener((MainActivity)getActivity());
        _layout.findViewById(R.id.button_testsuite).setOnClickListener((MainActivity)getActivity());*/
    }
}
