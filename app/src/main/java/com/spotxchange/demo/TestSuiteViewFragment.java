package com.spotxchange.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.spotxchange.sdk.android.SpotxAdCallback;

import org.json.JSONObject;


public class TestSuiteViewFragment extends Fragment implements View.OnClickListener{

    RelativeLayout _layout;
    Button _btnOpenUrl;
    Button _btnSms;
    Button _btnCalendarEvent;
    Button _btnStorePicture;

    SpotxAdCallback _callback;

    JSONObject _calData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _layout = (RelativeLayout) inflater.inflate(R.layout.fragment_test_suite, container, false);

        _btnCalendarEvent = (Button) _layout.findViewById(R.id.button_calendar_event);
        _btnOpenUrl = (Button) _layout.findViewById(R.id.button_open_url);
        _btnSms = (Button) _layout.findViewById(R.id.button_sms);
        _btnStorePicture = (Button) _layout.findViewById(R.id.button_store_picture);

        _btnCalendarEvent.setOnClickListener(this);
        _btnOpenUrl.setOnClickListener(this);
        _btnSms.setOnClickListener(this);
        _btnStorePicture.setOnClickListener(this);

        _callback = new SpotxAdCallback(null, getActivity());
        _calData = new JSONObject();

        try {
            _calData.put("description", getString(R.string.test_mraid_cal_description));
            _calData.put("location", getString(R.string.test_mraid_cal_location));
            _calData.put("start", getString(R.string.test_mraid_cal_start_time));
            _calData.put("end", getString(R.string.test_mraid_cal_end_time));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return _layout;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_open_url:
                _callback.openUrl(getString(R.string.test_mraid_open_url));
                break;
            case R.id.button_sms:
                _callback.sendSms(getString(R.string.test_mraid_sms));
                break;
            case R.id.button_calendar_event:
                _callback.createCalendarEvent(_calData);
                break;
            case R.id.button_store_picture:
                _callback.storePicture(getString(R.string.test_mraid_picture_url));
                break;
        }
    }
}
