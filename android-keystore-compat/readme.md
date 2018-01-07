# KeystoreCompat


[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


```
If your application supports API lower than 19, include this line to app's AndroidManifest file:
  
<uses-sdk tools:overrideLibrary="cz.koto.keystorecompat" />

```

If you wan't to optimize bundled legacy code, use variant of this library:

| KeystoreCompat variant        | Readme  															| JCenter 											 |
| ----------------------------- | ----------------------------------------------------------------- | -------------------------------------------------- |
| KeystoreCompat L+      		| [KeystoreCompat-elPlus](android-keystore-compat-elplus/readme.md) | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion) |
| KeystoreCompat M+				| [KeystoreCompat-emPlus](android-keystore-compat-emplus/readme.md) | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion) |


<br/><br/>
This wrapper library is designed to save secret
to local shared preferences using Android default security and Android Keystore.
KeystoreCompat ensures handling LockScreen and compatibility among diversity of Android versions.

KeystoreCompat also keeps in mind existing Keystore-related vulnerabilities
and therefore follow the minimum API/rooted device detection and also inform about existing caveats/enhancements in all supported API version.

![KeystoreCompat princip](./extras/diagram/KeystoreCompat.png "KeystoreCompat princip")

## UseCase ##

Does your app use classic credentials (e.g. username & password / JWT / hash) to connect to secured part of the API OR encrypted database?


**Want to let user access your application using just Android default security**
(PIN/password/gesture/fingerprint) and do not force let user type username/password again and again?

**Or want to let application use keystore without using lockScreen?**
  
**If so, this library is designed for you!**

Sample application available on Github (also distributed via Google Play)
<br/> * [Realm Security - java project](https://github.com/kotomisak/db-showcase-android)
<br/> * [App login security - kotlin project](https://github.com/kotomisak/security-showcase-android)


## Install ##

Keystore compat is modular system and it requires to add `cz.koto:android-keystore-compat` and all
necessary dependent modules.

```groovy
dependencies {
	api("cz.koto:android-keystore-compat:2.0.1") {
		exclude group: 'com.android.support'
	}
	api("cz.koto:android-keystore-compat-base:2.0.1")
	api("cz.koto:android-keystore-compat-19:2.0.1")
	api("cz.koto:android-keystore-compat-21:2.0.1")
	api("cz.koto:android-keystore-compat-23:2.0.1")
	implementation('com.scottyab:rootbeer-lib:0.0.6') {
		exclude group: 'com.android.support'
	}
}
```
## Usage ##

**TODO here will be GIST for basic usage** 



## Caveats ##

The Keystore itself is encrypted using the user’s own lockscreen pin/password,
hence the device screen is locked the Keystore is unavailable.
Keep this in mind if you have a background service that could need to access your application secrets.

The Keystore can be lost anytime! Permament content is not guaranteed.

Security trust of Keystore grows with every new Android version.
KeystoreCompat library suggest usage is since API23(Android M), but support usage since API19(Android KitKat).
Every keystore is breakable (at least when device is rooted).

## Licence ##
The Apache Software License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.txt

