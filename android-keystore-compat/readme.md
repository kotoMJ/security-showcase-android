# KeystoreCompat #
[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto.misak/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto.misak/android-keystore-compat/_latestVersion)
<br/><br/>
This simple wrapper library is designed to save secret
to local shared preferences using Android default security and Android Keystore.
KeystoreCompat ensures handling LockScreen and compatibility among diversity of Android versions.


KeystoreCompat also keeps in mind existing Keystore-related vulnerabilities
and therefore follow the minimum API/rooted device detection and also inform about existing caveats/enhancements in all supported API version.

![KeystoreCompat princip](./extras/diagram/KeystoreCompat.png "KeystoreCompat princip")

## Kotlin language ##
KeystoreCompat library is written in Kotlin language.

So in case of usage this library from java project (without existing dependency on Kotlin) you need also
to add dependency on `org.jetbrains.kotlin:kotlin-stdlib:1.0.6` (over **5k method on dex**)

If it is too much for your java project, **feel free to grab constructions from this library to achieve the same functionality without dependency on Kotlin**.

## UseCase ##

Does your app use classic credentials (e.g. username & password / JWT / hash) to connect to secured part of the API OR encrypted database?


**Want to let user access your application using just Android default security**
(PIN/password/gesture/fingerprint) and do not force let user type username/password again and again?

**If so, this library is designed for you!**

Sample application available on Github (also distributed via Google Play)
<br/> * [Realm Security - java project](https://github.com/kotomisak/db-showcase-android)
<br/> * [App login security - kotlin project](https://github.com/kotomisak/security-showcase-android)

## Android keystore in existing libraries ##
https://github.com/Q42/Qlassified-Android - wrapper using the same under the hood approach as KeystoreCompat library,
but designed rather for saving encrypted data generally.<br/>
_In comparison:_ **KeystoreCompat: is designed rather to simplify work with the secret AND offer valuable functionality for work with the LockScreen (covering all API versions since 19)**(check e.g. [login credentials](https://github.com/kotomisak/security-showcase-android)).
For securing complex data to be stored rather permanently use KeystoreCompat with combination of secured persistence(chek e.g. [encrypting Realm](https://github.com/kotomisak/db-showcase-android))

## Omit the keystore approach ##
https://github.com/scottyab/secure-preferences - you can use encryption based on some phrase and encrypt data directly.
But be careful, this approach force developers handle with another secret (besides the own device secret) and list of
potential vulnerabilities will be always rather uknown than using the Android defaults.


## Installation ##

Add following dependency to your build.gradle: [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto.misak/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto.misak/android-keystore-compat/_latestVersion)
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
compile ('cz.koto.misak:android-keystore-compat:x.y.z') {
		exclude group: 'com.android.support'
	}
```

## Configuration ##
All mentioned configurations are voluntary (KeystoreCompat is shipped with default configuration).

### KeystoreCompatConfig ###
KeystoreCompat offer possibility to override default configuration using:
`cz.koto.misak.keystorecompat.KeystoreCompat.overrideConfig(T : KeystoreCompatConfig)`

- `fun getDialogDismissThreshold(): Int` Define how many times can be screenLock/KitKatAdmin dialog displayed when it was previously cancelled.
- `open fun isRootDetectionEnabled(): Boolean` You can disable root detection by this method, but it is on your risk (**it's good e.g. for debug variant because of Emulator**)!

In case of overriding KeystoreCompatConfig, call overrideConfig method before the first KeystoreCompat usage.

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

## More about Android keystore ##

Android keystore is evolving mechanism from one Android version to other.
This library ensure handling Android Keystore since Android API19 (KitKat).




### Android keystore usability - unstable storage ###
Keep in mind, that [Android keystore can delete all keys](https://code.google.com/p/android/issues/detail?id=61989)
if you change screen lock type (or update fingerprint/PIN/Password/Gesture).
[It practically means information encrypted using keystore can be lost any time.](https://doridori.github.io/android-security-the-forgetful-keystore/#sthash.gFJfhQs6.dpbs)
Therefore it tends to use Android keystore rather as temporary secure storage.



### Android Keystore - min API ###
[The Keystore](https://developer.android.com/reference/java/security/KeyStore.html) itself has been available since API 1 (restricted to use by VPN and WiFi systems).

The [Android keystore System](https://developer.android.com/training/articles/keystore.html) which is typically
backed by hardware (but not necessarily so) was formally introduced in Android 4.3 /API 18

Because of [stack-based buffer overflow vulnerability](https://threatpost.com/patched-code-execution-bug-affects-most-android-users/106880/)
it is safer to use Android keystore since Android KitKat / API 19.

When you wanna use standard Android security screen (PIN/Password/Gesture/Fingerprint) the safe support is since Android Lollipop / API 21.

Enhanced/Improved security of Keystore is then since Android Marshmallow / API 23.

### Android keystore - enough secured for me? or NOT ? ###

**API 19(KitKat)** - user has to grant DEVICE ADMIN rights for KeystoreCompat library in order make this library working relatively securely with API19.
If user don't grant device admin rights, library can't do the job.

**API 21(Lollipop)** - Since Lollipop is KeystoreCompat using standard Android's ScreenLock.
Force to display ScreenLock is still not defined in the certificate it self,
but user has to handle forcing LockScreen.

**API 23(Marshmallow)** - Since Marshmallow there is ScreenLock force ensured directly in the certificate definition.
Marshmallow targets to [hardware-backed keystore](https://source.android.com/security/keystore/) and bring lots of (look for 23+)
[security options](https://developer.android.com/training/articles/keystore.html).
The way of key-pair generation is completely new. Marshmallow also starts support fingerprint authentication.

**KeystoreCompat library suggests to use Keystore since `Marshmallow`, but supports usage since `KitKat`.**

### List of known vulnerabilites ###

#### 2016 July - Attacker can modify stored keys  ####

In an academic paper published in July 2016, researchers argue that the particular encryption scheme that KeyStore uses
[fails to protect the integrity of keys](https://threatpost.com/android-keystore-encryption-scheme-broken-researchers-say/119092/)
and could be exploited to allow an attacker to modify stored keys through a forgery attack.
KeyStore, which performs key-specific actions through the OpenSSL library,
allows Android apps to store and generate their own cryptographic keys.
By storing keys in a container, KeyStore makes it more difficult to remove them from the device.
Mohamed Sabt and Jacques Traore, two researchers with the French telecom Orange Labs,
claim the scheme associated with the system is "non-provably secure," and could have "severe consequences."
The two point out in their paper "Breaking Into the KeyStore: A Practical Forgery Attack Against Android KeyStore,"
that it's the hash-then-encrypt (HtE) authenticated encryption (AE) scheme in cipher block chaining mode (CBC)
in KeyStore that fails to guarantee the integrity of keys.

#### 2015 August - OpenSSLX509Certificate ####
The problem is in a single vulnerable class in the Android platform, called OpenSSLX509Certificate,
that the researchers were able to create an exploit for.<br/>
http://www.itworldcanada.com/post/new-android-vulnerability-could-give-attackers-full-privileges <br/>
The good news is that Google has fixed the two OpenSSLX509Certificate instances,
patched Android 5.1 ,5.0, Android M and backported the patch to Android 4.4.

#### 2014 June - stack-based buffer overflow vulnerability ####
A successful exploit would compromise a device completely,
allowing an attacker to execute code of their choosing under the keystore process.<br />
https://threatpost.com/patched-code-execution-bug-affects-most-android-users/106880/ <br/>
This serious code-execution vulnerability in Android 4.3 and earlier was patched in KitKat,
the latest version of the operating system.


### Keystore related articles ###
http://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/
https://threatpost.com/android-keystore-encryption-scheme-broken-researchers-say/119092/
https://duo.com/blog/more-than-half-of-android-phones-vulnerable-to-encryption-bypass-attacks
https://doridori.github.io/android-security-the-forgetful-keystore/#sthash.gFJfhQs6.dpbs
https://crackstation.net/hashing-security.htm
https://www.owasp.org/index.php/Hashing_Java


