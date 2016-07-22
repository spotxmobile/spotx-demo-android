package com.spotxchange.brightcove.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.SourceAwareMetadataObject;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveVideoView;
import com.spotxchange.v3.adapters.brightcove.SpotXBrightcoveAdapter;
import com.spotxchange.v3.SpotX;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String _channelId = "85394";
    private BrightcoveVideoView _videoView;
    private EventEmitter _eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.spotxchange.brightcove.demo.R.layout.activity_main);
        setup();
        playContent();
    }

    /**
     * Sets up the BrightcoveVideoView and SpotX
     */
    private void setup() {
        // video view
        _videoView = (BrightcoveVideoView) findViewById(com.spotxchange.brightcove.demo.R.id.brightcove_video_view);
        _eventEmitter = _videoView.getEventEmitter();

        // spotx
        SpotX.initialize(this); // needs to be called before creating the SpotXBrightcoveAdapter
        new SpotXBrightcoveAdapter(_eventEmitter, _videoView, _channelId);

        // listen for the content source to be set to create ad cue points
        _eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Source source = (Source) event.properties.get(Event.SOURCE);
                setupCuePoints(source);
            }
        });
    }

    /**
     * Creates the Video from the given MP4 and plays the content in the BrightcoveVideoView
     */
    private void playContent() {
        Video bigBuckBunnyVideo = Video.createVideo("https://spotxchange-a.akamaihd.net/media/videos/orig/d/3/d35ba3e292f811e5b08c1680da020d5a.mp4");
        _videoView.add(bigBuckBunnyVideo);
        _videoView.start();
    }

    /**
     * Creates the CuePoints at points in time relative to the content video on when an Ad will play
     *
     * @param source
     */
    private void setupCuePoints(Source source) {
        final String cuePointType = "AD";
        CuePoint cuePoint;
        Map<String, Object> properties = new HashMap<>();

        // create a pre-roll
        cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, Collections.<String, Object>emptyMap());
        properties.put(Event.CUE_POINT, cuePoint);
        _eventEmitter.emit(EventType.SET_CUE_POINT, properties);

        // create a mid-roll at the 15 second mark (mid-rolls don't work with HLS videos due to an Android bug).
        if (!DeliveryType.HLS.equals(source.getProperties().get(SourceAwareMetadataObject.Fields.DELIVERY_TYPE))) {
            cuePoint = new CuePoint(15000, cuePointType, Collections.<String, Object>emptyMap());
            properties.put(Event.CUE_POINT, cuePoint);
            _eventEmitter.emit(EventType.SET_CUE_POINT, properties);
        }

        // create a post-roll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, Collections.<String, Object>emptyMap());
        properties.put(Event.CUE_POINT, cuePoint);
        _eventEmitter.emit(EventType.SET_CUE_POINT, properties);
    }

}
