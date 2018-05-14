package com.spotxchange.testapp;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.SourceAwareMetadataObject;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import com.spotxchange.v4.SpotXAdRequest;
import com.spotxchange.v4.adapters.brightcove.SpotXBrightcoveAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrightcovePlayerActivity extends BrightcovePlayer {

    private static final String TAG = BrightcoveFragment.class.getSimpleName();
    private BrightcoveVideoView _videoView;
    private EventEmitter _eventEmitter;
    private boolean _hasSetCuePoints;

    private Settings _settings;

    // For fullscreen mode
    private Toolbar _toolbar;
    private RelativeLayout _layout;
    private Rect _layoutPadding;
    private Drawable _layoutBackground;
    private RelativeLayout.LayoutParams _videoLayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightcove_player);

        _layout = (RelativeLayout)findViewById(R.id.brightcove_layout);

        _toolbar = (Toolbar)findViewById(R.id.toolbar);
        _toolbar.setTitle(R.string.title_activity_brightcoveplayer);

        _videoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        brightcoveVideoView = _videoView;
    }

    @Override
    protected void onStart() {
        super.onStart();

        setup();
        loadContent();
        _videoView.start();
    }

    /**
     * Sets up the BrightcoveVideoView and SpotX.
     */
    private void setup() {
        // video view
        _eventEmitter = _videoView.getEventEmitter();

        // spotx
        _settings = new Settings(this);
        SpotXAdRequest request = _settings.requestWithSettings();
        new SpotXBrightcoveAdapter(_eventEmitter, _videoView, request);

        // listen for the content source to be set to create ad cue points
        _eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Source source = (Source) event.properties.get(Event.SOURCE);
                setupCuePoints(source);
            }
        });

        _eventEmitter.on(EventType.ENTER_FULL_SCREEN, new EventListener() {
            @Override
            public void processEvent(Event event) {
                enterFullscreen();
            }
        });

        _eventEmitter.on(EventType.DID_EXIT_FULL_SCREEN, new EventListener() {
            @Override
            public void processEvent(Event event) {
                exitFullscreen();
            }
        });
    }

    private void enterFullscreen() {
        // Remove padding
        _toolbar.setVisibility(View.GONE);
        _layoutPadding = new Rect(_layout.getPaddingLeft(), _layout.getPaddingTop(),
                _layout.getPaddingRight(), _layout.getPaddingBottom());
        _layout.setPadding(0, 0, 0, 0);

        // Change background to black
        _layoutBackground = _layout.getBackground();
        _layout.setBackgroundColor(Color.BLACK);

        // Set video player to fill parent
        _videoLayoutParams = (RelativeLayout.LayoutParams)_videoView.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        _videoView.setLayoutParams(layoutParams);
    }

    private void exitFullscreen() {
        // Reset padding
        _toolbar.setVisibility(View.VISIBLE);
        _layout.setPadding(_layoutPadding.left, _layoutPadding.top,
                _layoutPadding.right, _layoutPadding.bottom);

        // Set background back to white
        _layout.setBackground(_layoutBackground);

        // Restore video player size
        _videoView.setLayoutParams(_videoLayoutParams);
    }

    /**
     * Creates the Video from the given MP4 and puts the content in the BrightcoveVideoView.
     */
    private void loadContent() {
        Video bigBuckBunnyVideo = Video.createVideo("https://spotxchange-a.akamaihd.net/media/videos/orig/d/3/d35ba3e292f811e5b08c1680da020d5a.mp4");
        _videoView.add(bigBuckBunnyVideo);
    }

    /**
     * Creates the CuePoints at points in time relative to the content video on when an Ad will play.
     *
     * @param source
     */
    private void setupCuePoints(Source source) {
        if (_hasSetCuePoints) {
            return;
        }
        _hasSetCuePoints = true;
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
