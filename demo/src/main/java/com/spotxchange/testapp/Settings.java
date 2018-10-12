package com.spotxchange.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.spotxchange.v4.SpotXAdRequest;

public class Settings {

    // Application API key
    public static final String API_KEY                  = "apikey-1234";

    // Default channel
    public static final String DEFAULT_CHANNEL_ID       = "85394";

    // Standard preferences
    public static final String KEY_CHANNEL_ID           = "spotxChannel";
    public static final String KEY_PRESENTATION         = "spotxPresentationType";

    public static final String KEY_VPAID                = "spotxUseVPAID";

    public static final String KEY_POD_ENABLE           = "spotxUsePodding";
    public static final String KEY_POD_COUNT            = "spotxAdCount";
    public static final String KEY_AD_DURATION          = "spotxAdDuration";
    public static final String KEY_POD_DURATION         = "spotxPodDuration";

    public static final String KEY_GDPR_ENABLE          = "spotxGDPREnable";
    public static final String KEY_GDPR_CONSENT         = "spotxGDPRConsent";

    // MoPub Ad Units
    public static final String KEY_MOPUB_INTERSTITIAL   = "spotxMoPubI";
    public static final String KEY_MOPUB_REWARDED       = "spotxMoPubR";

    // GMA IDs
    public static final String KEY_GMA_APP              = "spotxGmaA";
    public static final String KEY_GMA_INTERSTITIAL     = "spotxGmaI";
    public static final String KEY_GMA_REWARDED         = "spotxGmaR";

    private SharedPreferences _pref;

    /** New Settings object with the given context. */
    public Settings(Context context) {
        _pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Creates a SpotXAdRequest with standard settings applied to it.
     *
     * @return the ad request.
     * */
    public SpotXAdRequest requestWithSettings() {
        SpotXAdRequest request = new SpotXAdRequest(API_KEY);
        request.channel = getString(KEY_CHANNEL_ID, DEFAULT_CHANNEL_ID);

        // This is how to use VPAID
        if (getBoolean(KEY_VPAID)) {
            request.putParam("VPAID", "js");
        }

        // This is how to set up podding
        if (getBoolean(KEY_POD_ENABLE)) {
            int podSize = Integer.valueOf(getString(KEY_POD_COUNT));
            int adDuration = Integer.valueOf(getString(KEY_AD_DURATION));
            int podDuration = Integer.valueOf(getString(KEY_POD_DURATION));

            request.putCustom("pod[size]", podSize);
            request.putCustom("pod[max_ad_dur]", adDuration);
            request.putCustom("pod[max_pod_dur]", podDuration);
        }

        // This is how to enable GDPR with a consent string:
        if (getBoolean(KEY_GDPR_ENABLE)) {
            String gdprConsent = getString(KEY_GDPR_CONSENT);

            SharedPreferences.Editor editor = _pref.edit();
            editor.putString("IABConsent_SubjectToGDPR", "1");
            editor.putString("IABConsent_ConsentString", gdprConsent);
            editor.commit();
        } else {
            // Need to remove GDPR from the SharedPreferences, or they'll be used the next time the demo is run
            SharedPreferences.Editor editor = _pref.edit();
            editor.remove("IABConsent_SubjectToGDPR");
            editor.remove("IABConsent_ConsentString");
            editor.commit();
        }

        // This is what KVPs look like where `multiple=false`
        // request.putCustom("custom[production_year]", "1999");

        // This is what KVPs look like where `multiple=true`
        // request.putCustom("custom[production_year]", "1999");
        // request.putCustom("custom[trucks]", Arrays.asList("Ford", "Toyota", "Chevrolet"));

        // This is what Custom Macros look like
        // request.putCustom("token[cm-key]", "cm-value");
        // request.putCustom("token[cms-key]", Arrays.asList("cms-value1", "cms-value2", "cms-value3"));

        return request;
    }

    /** Retrieves a string value with a default. */
    public String getString(String name, String defValue) {
        String res = _pref.getString(name, defValue);
        if (res == null || res.length() == 0) {
            res = defValue;
        }
        return res;
    }

    /** Retrieves a string value. */
    public String getString(String name) {
        return _pref.getString(name, "");
    }

    /** Retrieves a boolean value with a default. */
    public Boolean getBoolean(String name, boolean defValue) {
        return _pref.getBoolean(name, defValue);
    }

    /** Retrieves a boolean value with default = false. */
    public Boolean getBoolean(String name) {
        return _pref.getBoolean(name, false);
    }

    /** Retrieves an integer value. */
    public Integer getInt(String name) {
        return _pref.getInt(name, 0);
    }

    /** Sets a string value. */
    public void set(String name, String value) {
        SharedPreferences.Editor editor = _pref.edit();
        if (value == null) {
            editor.remove(name);
        } else {
            editor.putString(name, value);
        }
        editor.commit();
    }

    /** Sets a boolean value. */
    public void set(String name, Boolean value) {
        SharedPreferences.Editor editor = _pref.edit();
        if (value == null) {
            editor.remove(name);
        } else {
            editor.putBoolean(name, value);
        }
        editor.commit();
    }

    /** Sets an integer value. */
    public void set(String name, Integer value) {
        SharedPreferences.Editor editor = _pref.edit();
        if (value == null) {
            editor.remove(name);
        } else {
            editor.putInt(name, value);
        }
        editor.commit();
    }

}
