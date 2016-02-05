package com.spotxchange.demo.brightcove;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import com.spotxchange.demo.R;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.brightcoveplugin.SpotxBrightcovePlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrightcoveTestActivity extends BrightcovePlayer implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName();

    private CheckBox _preroll;
    private CheckBox _midroll;
    private CheckBox _postroll;
    private EditText _channelText;
    private Button _playBtn;

    private CuePoint _cue;
    private SpotxBrightcovePlugin _plugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_brightcove);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.video_view);
        super.onCreate(savedInstanceState);

        _preroll = (CheckBox) findViewById(R.id.prerollCheckBox);
        _midroll = (CheckBox) findViewById(R.id.midrollCheckBox);
        _postroll = (CheckBox) findViewById(R.id.postrollCheckBox);
        _channelText = (EditText) findViewById(R.id.channelText);
        _playBtn = (Button) findViewById(R.id.playButton);
        _playBtn.setOnClickListener(this);
    }

    private void setupCuePoints() {
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<>();
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        // preroll
        if(_preroll.isChecked()) {
            _cue = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, Collections.<String, Object>emptyMap());
            properties.put(Event.CUE_POINT, _cue);
            eventEmitter.emit(EventType.SET_CUE_POINT, properties);
        }

        // midroll (5 sec)
        if(_midroll.isChecked()) {
            _cue = new CuePoint(5000, cuePointType, Collections.<String, Object>emptyMap());
            properties.put(Event.CUE_POINT, _cue);
            eventEmitter.emit(EventType.SET_CUE_POINT, properties);
        }

        // postroll
        if(_postroll.isChecked()) {
            _cue = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, Collections.<String, Object>emptyMap());
            properties.put(Event.CUE_POINT, _cue);
            eventEmitter.emit(EventType.SET_CUE_POINT, properties);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.playButton:
                // Instantiate the SpotX plugin.
                _plugin = new SpotxBrightcovePlugin(brightcoveVideoView.getEventEmitter(), this, brightcoveVideoView);
                SpotxAdSettings adSettings = new SpotxAdSettings(getChannelIdFromEditText(), "spotxchange.com");
                _plugin.init(adSettings);

                // Request a sample playlist from the Brightcove Media API.
                Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
                catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, error);
                    }

                    @Override
                    public void onPlaylist(Playlist playlist) {
                        brightcoveVideoView.addAll(playlist.getVideos());
                        brightcoveVideoView.start();
                    }
                });

                brightcoveVideoView.getEventEmitter().on(EventType.DID_SET_SOURCE, new EventListener() {
                    @Override
                    public void processEvent(Event event) {
                        setupCuePoints();
                    }
                });
                break;
        }
    }

    public int getChannelIdFromEditText() {
        int result = -1;
        try {
            String channelIdString = (_channelText.getText().toString().isEmpty() || _channelText.getText() == null) ? _channelText.getHint().toString() : _channelText.getText().toString();
            result = Integer.parseInt(channelIdString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        _plugin.remove();
        _plugin.removeListeners();
        _plugin = null;
        brightcoveVideoView.stopPlayback();
        brightcoveVideoView.removeListeners();
        brightcoveVideoView = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}