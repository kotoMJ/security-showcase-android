# KeystoreCompat #

[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto.misak/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto.misak/android-keystore-compat/_latestVersion)
<br/><br/>
This simple wrapper library is designed to save credentials
to local shared preferences (encrypted using Android default security and Android Keystore).

KeystoreCompat also keeps in mind existing Keystore-related vulnerabilities
and therefore follow the minimum API and also inform about existing caveats/enhancements in all supported API version.

## UseCase ##

Does your app use classic credentials (e.g. username & password) to connect to secured part of the app?

**Want to let user access your application using just Android default security**
(PIN/password/gesture/fingerprint) and do not force let user type username/password again and again?

**If so, this library is designed for you.**

## Installation ##
Minimum API is 19!
Running on lower version will not crash, but will do nothing.

Initialize KeystoreCompat in your application class.
`cz.koto.misak.keystorecompat.KeystoreCompat.init(this)`

## Usage ##
For detail usage check for sample implementation in SecurityShowcase application

### KeystoreCompat verify methods ###
- `fun isKeystoreCompatAvailable(): Boolean`
- `fun isSecurityEnabled(): Boolean`
- `fun hasCredentialsLoadable(): Boolean`

### KeystoreCompat data manipulation methods ###
- `fun storeCredentials(composedCredentials: String, onError: () -> Unit)`
- `fun loadCredentials(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?)`
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

## Caveats ##

The Keystore itself is encrypted using the user’s own lockscreen pin/password,
hence, when the device screen is locked the Keystore is unavailable.
Keep this in mind if you have a background service that could need to access your application secrets.

The Keystore can be lost anytime! Permament content is not guaranteed.

Security trust of Keystore grows with every new Android version.
KeystoreCompat library suggest usage is since API23(Android M), but support usage since API19(Android KitKat).
Every keystore is breakable (at least when device is rooted).

## Android keystore in existing libraries ##
https://github.com/Q42/Qlassified-Android - wrapper using the same under the hood approach as KeystoreCompat library,
but designed rather for saving encrypted data generally.<br/>
_In comparison:_ **_KeystoreCompat_** _is designed rather for securing login related credentials only(see below mentioned chapter UNSTABLE STORAGE).
For securing complex data to be stored rather permanently use secured database, e.g. Realm.io._

## Omit the keystore approach ##
https://github.com/scottyab/secure-preferences - you can use encryption based on some phrase and encrypt data directly.
But be careful, this approach force user handle with another secret (besides the own device secret) and list of
potential vulnerabilities will be always rather uknown than using the Android defaults.

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
but user has to handle forcing LockScreen in `onFailure` block of `KeystoreCompat.loadCredentials` method.

**API 23(Marshmallow)** - Since Marshmallow there is ScreenLock force ensured directly in the certificate definition.
Marshmallow targets to [hardware-backed keystore](https://source.android.com/security/keystore/) and bring lots of (look for 23+)
[security options](https://developer.android.com/training/articles/keystore.html).
The way of key-pair generation is completely new. Marshmallow also starts support fingerprint authentication.

**KeystoreCompat library suggests to use Keystore since `Marshmallow`, but supports usage since `KitKat`.**

### List of known vulnerabilites ###

#### Attacker can modify stored keys  ####

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


### Keystore related articles ###
http://www.androidauthority.com/use-android-keystore-store-passwords-sensitive-information-623779/
https://threatpost.com/android-keystore-encryption-scheme-broken-researchers-say/119092/
https://duo.com/blog/more-than-half-of-android-phones-vulnerable-to-encryption-bypass-attacks
https://doridori.github.io/android-security-the-forgetful-keystore/#sthash.gFJfhQs6.dpbs
..


