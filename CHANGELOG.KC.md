
# Release notes for Keystore Compat library

## Not released yet
 * Use advanced SafetyNet detection of rooted device.
 * Use AES for keypair-generation since Android M

## KC-1.0.0-BETA
 * API 23+(Android M and above) is now using AES instead of RSA.
 * New configurable parameter in KeystoreCompatConfig: UserAuthenticationValidityDurationSeconds
 * Breaking API change (caused by AES implementation in da library)
 	* Change package of the ForceLockScreenKitKatException (& add some other helpful exceptions)
 	* onError method of both storeSecret() now returns derived KeystoreCompatException

## KC-0.6.0-BETA
 * Add new feature to disable KeystoreCompat (by default) when rooted device is [detected](https://github.com/scottyab/rootbeer).

## KC-0.5.0-BETA
 * API of KeystoreCompat is refactored according to new experience during implementation of [Support for encrypted Realm](https://github.com/kotomisak/db-showcase-android)

## KC-0.4.0-BETA
 * First stable version ready for usage in Security Showcase app.
