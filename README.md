Android Test App Documentation
==============================

Example Android app using the SpotXchange Android SDK.

Prerequisites
-------------

  * Minimum API: 15 (Ice Cream Sandwich)
  * Target API: 19 (KitKat)

Before you begin
----------------
  1. You'll need to apply to become a SpotX publisher if you haven't already. 
You'll receive the SpotXchange's AdManager Framework/Library, a publisher channel ID, 
and an account to log in to SpotXchange's Publisher Tools (https://publisher.spotxchange.com/).
  2. Locate "SpotXMraid.jar" inside this project. This contains the SpotXchange MRAID Library.

You can always download the latest version of the SpotX MRAID library using this link:
[http://search.spotxchange.com/ad_player/AndroidSDK.jar](http://search.spotxchange.com/ad_player/AndroidSDK.jar)
or on GitHub:
[https://github.com/spotxmobile/spotx-sdk-android](https://github.com/spotxmobile/spotx-sdk-android)

The latest version of the SpotXchange Android Test App is always available on GitHub at:
[https://github.com/spotxmobile/spotx-testapp-android](https://github.com/spotxmobile/spotx-testapp-android)

Setup for Android Studio
------------------------

  1. Download the Android Test App.

  2. From Android Studio, choose "Import Project..." and select the directory where the Android Test App is located.
  Import this project as you normally would.

  3. Select the activity type you are interested in (inline, interstitial, etc).
  For more information about the SpotXchange SDK API, refer to [the SDK documentation](https://github.com/spotxmobile/spotx-sdk-android)

  4. Input your channel id into the text area, then press the button to launch the activity.

Frequently Asked Questions
--------------------------

  * How do I get more information about the behavior of my ad?

  In the test app, select "Debug Information", then "Set Debug Cookie". This will cause additional information to be output
  to your webview's console.
