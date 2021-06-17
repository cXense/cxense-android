# Piano SDK for Android

## v2.2.0-SNAPSHOT
* Updated dependencies:
    - Kotlin [1.4.32 -> 1.5.10]
    - androidx.activity:activity [1.2.2 -> 1.2.3]
      https://developer.android.com/jetpack/androidx/releases/activity#1.2.3
    - androidx.appcompat:appcompat [1.2.0 -> 1.3.0]
      https://developer.android.com/jetpack/androidx/releases/appcompat#1.3.0
    - androidx.fragment:fragment [1.3.2 -> 1.3.4]
      https://developer.android.com/jetpack/androidx/releases/fragment#1.3.4
    - com.android.tools.build:gradle [4.1.3 -> 4.2.1]
      http://tools.android.com/
    - com.facebook.android:facebook-login [9.1.0 -> 11.0.0]
      https://github.com/facebook/facebook-android-sdk
* Removed deprecated Composer initialization

## v2.1.1
* Fixed small bug with production-australia, production-asia-pacific endpoints in Composer

## v2.1.0

#### Composer
* Changed endpoint structure
* Added predefined endpoints (production, production-australia, production-asia-pacific, sandbox)
* Added `meterName` and `incremented` parameters for `Meter` event type
* Added ability to set webview provider for inline show template

#### ID
* Migrated to [Activity Result API](https://developer.android.com/training/basics/intents/result)
* Added possibility to provide custom `JavascriptInterface` with `customEvent` handler
* Added flag `isNewUser`. If true, user has just registered, otherwise it's false
* Changed result type from `PianoIdToken` to `PianoIdAuthResult`, which contains `token` and flag `isNewUser`
