# KeystoreCompat #

This simple wrapper library is designed to save credentials
to local shared preferences (encrypted using Android default security and Android Keystore).

## UseCase ##

Does your app use classic credentials (e.g. username & password) to connect to secured part of the app.
`Want to let user access your application using just Android default security`
(PIN/password/gesture/fingerprint) and do not force let user type username/password again and again?

`If so, this library is designed for you.`

## Installation ##

TBD. (temporarily look for installation in security-showcase-android app)

## Usage ##

TBD. (temporarily look for usage in security-showcase-android app)

## Android keystore in existing libraries ##
https://github.com/Q42/Qlassified-Android - wrapper using the same under the hood approach as KeystoreCompat library,
but designed rather for saving encrypted data generally.
KeystoreCompat is designed rather for store credentials (for login to secured part of the app).


## Licence ##
TBD.

## More about Android keystore ##

Android keystore is evolving mechanism from one Android version to other.
This library concentrate handling Android keystore for all Android versions
(of course starting with API when Keystore is relatively safe for use).



### Android keystore usability - unstable storage ###
Keep in mind, that [Android keystore delete all keys](https://code.google.com/p/android/issues/detail?id=61989)
if you change screen lock type (or update fingerprint/PIN/Password/Gesture).
It practically means information encrypted using keystore can be lost any time.
Therefore it tends to use Android keystore rather as temporary secure storage.



### Android keystore - min API ###
[Android keystore](https://developer.android.com/training/articles/keystore.html) is relatively usable since Android 4.3 /API 18 when it was introduced
Android keystore provider

Because of some known security issues it is safer to use Android keystore since Android KitKat / API 19.

When you wanna use standard Android security screen (PIN/Password/Gesture/Fingerprint) the safe support is since Android Lollipop / API 21.

Enhanced/Improved security of keystore is then since Android Marshmallow / API 23.

### Android keystore - enough secured for me? or NOT ? ###

Using Android keystore itself has some known issues.
The more you know them, the better you can decide whether to use keystore or not (in some use-cases these issues not matter).


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



