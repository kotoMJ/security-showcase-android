# KeystoreCompat M+


[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat-emplus/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


```
If your application supports API lower than 23, include this line to app's AndroidManifest file:
  
<uses-sdk tools:overrideLibrary="cz.koto.keystorecompat" />

```

<br/><br/>
This wrapper library is designed to save secret
to local shared preferences using Android default security and Android Keystore.
KeystoreCompat ensures handling LockScreen and compatibility among diversity of Android versions.

KeystoreCompat also keeps in mind existing Keystore-related vulnerabilities
and therefore follow the minimum API/rooted device detection and also inform about existing caveats/enhancements in all supported API version.

![KeystoreCompat princip](./extras/diagram/KeystoreCompat.png "KeystoreCompat princip")

## Kotlin language ##
KeystoreCompat library is written in Kotlin language.

So in case of usage this library from java project (without existing dependency on Kotlin) you need also
to add dependency on `org.jetbrains.kotlin:kotlin-stdlib:1.1.51` (over **5k method on dex**)

If it is too much for your java project, **feel free to grab constructions from this library to achieve the same functionality without dependency on Kotlin**.

## UseCase ##

Does your app use classic credentials (e.g. username & password / JWT / hash) to connect to secured part of the API OR encrypted database?


**Want to let user access your application using just Android default security**
(PIN/password/gesture/fingerprint) and do not force let user type username/password again and again?

**Or want to let application use keystore without using lockScreen?**
  
**If so, this library is designed for you!**

Sample application available on Github (also distributed via Google Play)
<br/> * [Realm Security - java project](https://github.com/kotomisak/db-showcase-android)
<br/> * [App login security - kotlin project](https://github.com/kotomisak/security-showcase-android)


## Installation ##

Add following dependency to your build.gradle: [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion)
<br/>
Minimum API is 19!
Running on lower version will not crash, but will do nothing.
<br/>
Rooted device is NOT supported as trusted for KeystoreCompat (in default configuration).
Running on rooted device will not crash, but will do nothing and will return isKeystoreCompatAvailable()==false

KeystoreCompat initialize itself automatically with hosted application context.
The only pre-condition is, that hosted application has applicationId defined.

## Dependency cleanup ##
KeystoreCompat is using [RootBeer](https://github.com/scottyab/rootbeer) library to detect some signs of rooted device.
Used RootBeer library has dependency on old appCompat(appcompat-v7:22.2.0, support-annotations:22.2.0, support-v4:22.2.0).
If you wanna to avoid eventual library clash, use exclude as this:

```
compile ('cz.koto:android-keystore-compat:x.y.z') {
		exclude group: 'com.android.support'
	}
```

## Configuration ##
All mentioned configurations are voluntary (KeystoreCompat is shipped with default configuration).

### KeystoreCompatConfig ###
KeystoreCompat offer possibility to override default configuration using:
`cz.koto.keystorecompat.KeystoreCompat.overrideConfig(T : KeystoreCompatConfig)`

- `fun getDialogDismissThreshold(): Int` Define how many times can be screenLock/KitKatAdmin dialog displayed when it was previously cancelled.
- `open fun isRootDetectionEnabled(): Boolean` Disable root detection by this method, but it is on your risk (**it's good e.g. for debug variant because of Emulator**)!
- `open fun getUserAuthenticationRequired(): Boolean` **Disable keypair AndroidSecurity** to force user to authenticate itself when touching keypair.

In case of overriding KeystoreCompatConfig, call overrideConfig method before the first KeystoreCompat usage!

If you want to **disable lockScreen**, besides getUserAuthenticationRequired don't forget also parametrize loadSecret() with `forceFlag==false`

### String resources ###
Define customized strings in your application string.xml
<br/><br/>
`<string name="kc_lock_screen_title">Custom lock screen title</string>`
<br/><br/>
`<string name="kc_lock_screen_description">Custom lock screen description</string>`
<br/><br/>
`<string name="kc_kitkat_admin_explanatory">"Custom explanatory, explain to the user, that your application needs DeviceAdmin rights. For API 19 (KitKat) only."</string>`

## KeystoreCompat API

For detail usage check for sample implementations :<br/>
 [App login security - kotlin project](https://github.com/kotomisak/security-showcase-android) <br/>
 [Realm Security - java project](https://github.com/kotomisak/db-showcase-android) <br/>

### KeystoreCompat verify methods ###
- `fun isKeystoreCompatAvailable(): Boolean`
- `fun isSecurityEnabled(): Boolean`
- `fun hasSecretLoadable(): Boolean`

### KeystoreCompat data manipulation methods ###
- `fun storeSecret(secret: ByteArray, onError: () -> Unit, onSuccess: () -> Unit, useBase64Encoding: Boolean = true)`
- `fun storeSecret(secret: String, onError: () -> Unit, onSuccess: () -> Unit, useBase64Encoding: Boolean = true)`
- `fun loadSecret(onSuccess: (cre: ByteArray) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?, isBase64Encoded: Boolean = true)`
- `fun loadSecretAsString(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?, isBase64Encoded: Boolean = true)`
- `fun clearCredentials()`

### KeystoreCompat lockScreen dismiss helpers ###
- `fun increaseLockScreenCancel()`
- `fun signInSuccessful() `

### IntentUtility ###
- `inline fun showLockScreenSettings(context: Context)`
- @TargetApi(Build.VERSION_CODES.LOLLIPOP) <br/>
`inline fun forceAndroidAuth(title: String, desc: String, onIntentReady: (intent: Intent) -> Unit, context: Context)`

### AndroidVersionUtility ###
- `inline fun runSinceKitKat(crossinline action: () -> Unit)`
- `inline fun runSinceLollipop(crossinline action: () -> Unit)`
- `inline fun runSinceMarshmallow(crossinline action: () -> Unit)`

### HashUtility ###
- `fun createRandomHashKey(): ByteArray`
- `fun createHashKey(basePassword: String, salt: ByteArray, iterationCount: Int, sha512: Boolean, keyLengthInBit: Int = LENGTH32BYTES): ByteArray`
- `fun createHashKey(basePassword: String, sha512: Boolean, keyLengthInBit: Int = LENGTH32BYTES): ByteArray`

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

