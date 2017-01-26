# Security showcase #

![DbShowcase](./app/src/main/res/mipmap-hdpi/ic_launcher.png "DbShowcase") <a href="https://play.google.com/store/apps/details?id=cz.koto.misak.dbshowcase.android"><img src="./extras/banner/google-play-badge.png" height="72"/></a>

[![Build Status](https://travis-ci.org/kotomisak/security-showcase-android.svg)](https://travis-ci.org/kotomisak/security-showcase-android)

This is sample application pointing some security related practices on Android device.

## Build sample app ##

 `./gradlew assembleRostiRelease`

## Android keystore ##

SecurityShowcase application contains example of using standard Android Security with the Keystore.
All Android Keystore related stuff is bundled in KeystoreCompat library (available in this source code).

KeystoreCompat library should help to prevent pain when starting work with keystore from the official documentation
or StackOverflow discussion.

Read more about [KeystoreCompat](android-keystore-compat/readme.md)
