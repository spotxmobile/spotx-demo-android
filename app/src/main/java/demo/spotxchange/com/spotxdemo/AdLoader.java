package demo.spotxchange.com.spotxdemo;


import java.util.concurrent.TimeUnit;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAdBuilder;
import com.spotxchange.v3.SpotXAdGroup;

public class AdLoader extends AsyncTask<Void, Void, SpotXAdGroup> {

    public interface Delegate {
        void adLoadingStarted();
        void adLoadingFinished(@Nullable SpotXAdGroup adGroup);
    }

    private final Delegate _delegate;
    private final String _channelId;

    public AdLoader(@NonNull Delegate delegate, String channelId) {
        _delegate = delegate;
        _channelId = channelId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _delegate.adLoadingStarted();
    }

    @Override
    protected void onPostExecute(SpotXAdGroup adGroup) {
        super.onPostExecute(adGroup);
        _delegate.adLoadingFinished(adGroup);
    }

    @Override
    protected SpotXAdGroup doInBackground(Void... params) {
        SpotXAdBuilder request = SpotX.newAdBuilder(_channelId);
        try {
            return request.load().get(7, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            Log.e(AdLoader.class.getSimpleName(), "Unable to load SpotX Ad", e);
            return null;
        }
    }

}