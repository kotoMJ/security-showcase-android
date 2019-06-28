
# Release notes for Keystore Compat library
 
## KC-3.0.0
 ```
   KeystoreCompat 3.0.0 has NEW PACKAGE! cz.kotox:android-keystore-compat:3.0.0  
   ```
## KC-2.1.0
 * Fix crash when handling context in Android API19 (KitKat) - fix device admin dialog 
## KC-2.0.3
 * Prevent KeyNotValidException for some Huawei devices
## KC-2.0.2
 * Simplify installation configuration.
## KC-2.0.1
 * Connect impl libraries to JCenter.
## KC-2.0.0
 * Refactor structure to separate code specific for different API versions. KeystoreCompat can thus be composed 
 according to app requirements and doesn't force to depend on legacy code.
 * Original full version of KeystoreCompat is enhanced with KeystoreCompat L+ and KeystoreCompat M+.
 * KeystoreCompat is not distributed as static variable anymore. It's distributed as the object which should be 
 instantiated per application scope.
 
## KC-1.2.1
 * Add feature `KeystoreCompat.deactivate()` - this is currently implemented especially for API 4.4.4 where deactivation will disable device admin automatically. 
 Programmer therefore fulfill Google's fulfill device settings requirement. 
  
## KC-1.2.0
 ```
   KeystoreCompat 1.1.2 has NEW PACKAGE! cz.koto:android-keystore-compat:1.2.0
   ```
 
## KC-1.1.0
 * Upgrade dependency on Kotlin 1.1.1
 * Reduce number of required rights for API 4.4.4
 * Update RootBear lib to 0.0.6 

## KC-1.0.3-BETA
 * Fix invalidKey exception handling in case of Android-M and AES

## KC-1.0.2-BETA
 * Fix annoying error log of UserNotAuthenticatedException (since M it is part of the flow, log is therefore decreased to info without stacktrace)

## KC-1.0.1-BETA
 * Fix handling with private key entry in case of storeSecret with byteArray as the input

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
