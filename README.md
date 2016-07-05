# SpotX Android SDK Documentation

Example code and tutorial for displaying SpotX video ads in Android apps.

SDK & Javadocs are available on Bintray
* https://bintray.com/spotxmobile/maven/com.spotxchange%3Aspotx-sdk-android/view

## Prerequisites
* Minimum API: 14 (Ice Cream Sandwich)
* Target API: 24 (N)
* SpotX publisher account
  * [Apply to become a publisher](http://www.spotxchange.com/publishers/apply-to-become-a-spotx-publisher/)

## Before You Begin
1. You'll need to be a SpotX publisher if you aren't already.  You will
receive  the SpotX SDK, a publisher ID, and an account to log into the [SpotX
Publisher Tools](https://publisher.spotxchange.com).
2. From the Publisher Tools you will be able to create "channels" that define the
entry point to connecting with advertisers.  You'll need to create a channel
before you can use the SDK. Once a channel is created you will be given a
channel ID and you use this channel ID to integrate with the SDK.
3. Include the SpotX SDK as a dependency in your project in one of the following
ways:
  * Download the jar which contains the SpotX SDK Library, then - using Eclipse
  or Android Studio - declare that jar as a file dependency.
  * Using Gradle, include the following in your build.gradle file:

  ```
  repositories {
      jcenter()
  }
  dependencies {
        compile 'com.spotxchange:spotx-sdk-android:3.+'
  }
  ```
You can always download the latest version of the SpotX SDK from [Bintray](https://bintray.com/spotxmobile/maven/com.spotxchange%3Aspotx-sdk-android/view);
the latest version of the SpotX Demo App is also available on our [GitHub Repository](https://github.com/spotxmobile/spotx-demo-android).

## Setup Your Android Manifest
The `AndroidManifest.xml` file defines permissions for your applications.
1. Add `INTERNET` permission to the manifest file for your app, if it's not
already present.
```xml
<!--- REQUIRED FOR INTERNET ACCESS --->
<uses-permission android:name="android.permission.INTERNET"/>
```
2. Optionally, you may also include any or all the following permissions, which will
improve your ad revenue.
```xml
<!--  OPTIONAL PERMISSIONS -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

3. Add the Google Mobile Ads API by editing your `build.gradle` file add adding the line:
```java
dependencies {
       compile 'com.google.android.gms:play-services-ads:9.2.0'
}
```

4. Add the Google Play Services meta tag with the `<application>` tags.
```xml
<!-- Example application -->
<application
    android:allowBackup="true"
    android:icon="@drawable/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme" >
    <!-- REQUIRED -->
    <meta-data
        android:name="com.google.android.gms.version"
        android:value="@integer/google_play_services_version" />
</application>
```
4. You're now ready to integrate with the SpotX SDK. Refer to the selections
below for example integrations.

## Interstitial Ad Integration
An interstitial ad appears in a modal window, requiring the user to watch it
before they can access other content.
#### 1. Initialize the SpotX SDK

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  SpotX.initialize(getApplicationContext());
}
```
#### 2. Create a SpotXAdBuilder
The `SpotXAdBuilder` is a builder class that creates an ad request. The only
prerequisite to building an ad request is a channel id.  There are also methods
available to add additional ad parameters; for more information on supported ad
parameters see our [GitHub Documentation](https://github.com/spotxmobile/spotx-demo-android/wiki/Optional-Ad-Parameters).
The `SpotXAdBuilder` also supports custom parameters; custom parameters are
values that you can pass to us and report on later.  For more information on
custom parameters, please contact a SpotX representative.

##### SpotXAdBuilder Minimum Requirements
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
}
```

#### SpotXAdBuilder With Additional Ad Parameters
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
  sab.useHTTPS(true);
  sab.param("media_transcoding[]", new String[]{"low", "medium"});
}
```

#### SpotXAdBuilder With Custom Ad Parameters
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
  sab.custom("customParam", "customValue");
}
```

For the sake of conciseness the rest of this documentation will assume you
are doing a bare minimum integration.

#### 3. Load Your Ad
The `SpotXAdBuilder` has four load methods:
1. `load()` will load 1 ad.
2. `loadWithCount(int count)` will load `count` number of ads.
3. `loadWithDuration(double duration)` will load a number of ads whose cumulative duration is less than or equal to `duration`, in seconds.
4. `load(double duration, int count)` will load a maximum number of ads with a maximum cumulative duration.

The return value of any of our `load` functions is a `Future<SpotXAdGroup>`; by
returning a `Future<SpotXAdGroup>` we give you the ability to set up your
application without being blocked by us retrieving your ads. If you wanted to
ads to play in succession, your integration might look like:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
  Future<SpotXAdGroup> adGroupFuture = sab.loadWithCount(2);

  // Setup your app while we load ads in the background
}
```

#### 4. Get Your SpotXAdGroup
The `SpotXAdGroup` is a container holding all of your ads and all those ad's
information. When a `SpotXAdGroup` plays it will play all of your ads in
succession. To resolve a `Future<SpotXAdGroup` and get your `SpotXAdGroup` you
will want to call the future's `get` method. There are two `get` methods:
1. `get()` - blocks your application until the future resolves
2. `get(long timeout, TimeUnit unit)` - blocks until the future resolves or until
time has ran out.

It is strongly recommended that you use a timeout when resolving a future.
For more information review the [Android Future Documentation](https://developer.android.com/reference/java/util/concurrent/Future.html).

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
  Future<SpotXAdGroup> adGroupFuture = sab.loadWithCount(2);
  SpotXAdGroup adGroup = null;
  try {
    SpotXAdGroup adGroup = adGroupFuture.get(10000, TimeUnit.MILLISECONDS);
  }
  catch(Exception e) {
    // Handle exception
  }
  // Setup your app while we load ads in the background
 ```

#### 5. Display Your Ad
Once your have a `SpotXAdGroup` you can start playing ads. You can do so by
calling one of our presentation controllers. If you wanted to play an interstitial
ad your integration might look like:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
  Future<SpotXAdGroup> adGroupFuture = sab.loadWithCount(2);
  SpotXAdGroup adGroup = null;
  try {
    SpotXAdGroup adGroup = adGroupFuture.get(10000, TimeUnit.MILLISECONDS);
  }
  catch (Exception e) {
    // Handle exception
  }

  // Setup your app while we load ads in the background

  if (adGroup != null) {
    InterstitialPresentationController.show(getApplicationContext(), adGroup);
  }
```

## Advanced Usage
There may be times where you would like to listen to events. Events include:
* `onGroupStart()` - triggers before any ads starts playing
* `onStart(SpotXAd ad)` - triggers when a single ad starts playing
* `onError(SpotXAd ad)` - triggers if an error occurs during playtime
* `onSkip(SpotXAd ad)` - triggers if an ad gets skipped
* `onClick(SpotXAd ad)` - triggers if a user clicks through
* `onTimeUpdate(SpotXAd ad, int elapsed)` - triggers periodically and will return the current ad and the current elapsed time of the ad in milliseconds.
* `onComplete(SpotXAd)` - triggers when a single ad completes
* `onGroupComplete()` - triggers after all ads complete

We provide a `SpotXAdGroup.Observer` interface that you can implement to listen
to these events. If this is of interest to you, then your integration might look
like:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  String channelId = "12345";
  SpotX.initialize(getApplicationContext());
  SpotXAdBuilder sab = SpotX.newAdBuilder(channelId);
  Future<SpotXAdGroup> adGroupFuture = sab.loadWithCount(2);
  SpotXAdGroup adGroup = null;
  try {
    SpotXAdGroup adGroup = adGroupFuture.get(10000, TimeUnit.MILLISECONDS);
    adGroup.registerObserver(new MyObserver());
  }
  catch (Exception e) {
    // Handle exception
  }

  // Setup your app while we load ads in the background

  if (adGroup != null) {
    InterstitialPresentationController.show(getApplicationContext(), adGroup);
  }

  private class MyObserver implements SpotXAdGroup.Observer {
      @Override
      public void onGroupStart() {
          Toast.makeText(this, "Starting Group.", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStart(SpotXAd ad) {
          Toast.makeText(this, "Starting Ad", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onComplete(SpotXAd ad) {
          Toast.makeText(this, "Ad Complete", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onSkip(SpotXAd ad) {
          Toast.makeText(this, "Ad Skipped", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(SpotXAd ad, Error error) {
          Toast.makeText(this, "Ad Error", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onGroupComplete() {
          Toast.makeText(this, "Group completed.", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onTimeUpdate(SpotXAd ad, int elapsed) {
          // do nothing
      }

      @Override
      public void onClick(SpotXAd ad) {
          Toast.makeText(this, "Ad Clicked", Toast.LENGTH_SHORT).show();
      }
  }
}
```
