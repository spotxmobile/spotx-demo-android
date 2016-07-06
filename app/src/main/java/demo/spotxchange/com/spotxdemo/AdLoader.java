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

    public interface Callback {
        void adLoadingStarted();
        void adLoadingFinished(@Nullable SpotXAdGroup adGroup);
    }

    private final String _channelId;
    private final int _count;
    private final long _timeout;
    private final Callback _callback;


    public AdLoader(String channelId, int count, long timeout, @NonNull Callback callback) {
        _channelId = channelId;
        _count = count;
        _timeout = timeout;
        _callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _callback.adLoadingStarted();
    }

    @Override
    protected void onPostExecute(SpotXAdGroup adGroup) {
        super.onPostExecute(adGroup);
        _callback.adLoadingFinished(adGroup);
    }

    @Override
    protected SpotXAdGroup doInBackground(Void... params) {
        SpotXAdBuilder request = SpotX.newAdBuilder(_channelId);
        try {
            return request.loadWithCount(_count).get(_timeout, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            Log.e(AdLoader.class.getSimpleName(), "Unable to load SpotX Ad", e);
            return null;
        }
    }

}