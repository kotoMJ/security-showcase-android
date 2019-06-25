# Security showcase #

![SecurityShowcase](./app/src/main/res/mipmap-hdpi/ic_launcher.png "SecurityShowcase") <a href="https://play.google.com/store/apps/details?id=cz.koto.securityshowcase"><img src="./extras/banner/google-play-badge.png" height="72"/></a>  


[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

| Branch | Status |
| --- | --- |
| master | [![Build Status](https://travis-ci.org/kotomisak/security-showcase-android.svg?branch=master)](https://travis-ci.org/kotomisak/security-showcase-android) | 
| developV2| [![Build Status](https://travis-ci.org/kotomisak/security-showcase-android.svg?branch=developV2)](https://travis-ci.org/kotomisak/security-showcase-android) | 
| developV3| [![Build Status](https://travis-ci.org/kotomisak/security-showcase-android.svg?branch=developV3)](https://travis-ci.org/kotomisak/security-showcase-android) | 
This is sample application pointing some security related practices on Android device.  
SecurityShowcase architecture is **MVVM based on Google's architecture component**.  
Complete server-side endpoint implementation for this project is available on GitHub [KoTiNode](https://github.com/kotomisak/kotinode).

## Android keystore ##

[Android keystore basics](keystore.about.md) 

SecurityShowcase application contains example of using standard Android Security with the Keystore.
All Android Keystore related stuff is bundled in KeystoreCompat library (available in this source code).

KeystoreCompat library should help to prevent pain when starting work with keystore from the official documentation
or StackOverflow discussion. **The main point of this library is to provide the same services for all backward** (...Compat) **supported API versions (19+).**
The backward support is something, what all available libraries and blog posts lacks!

<br/>
[KeystoreCompat documentation](./android-keystore-compat/readme.md)   
<br/>  

| KeystoreCompat variant        | Readme  															| JCenter 											 |
| ----------------------------- | ----------------------------------------------------------------- | -------------------------------------------------- |
| KeystoreCompat default (K+)   | [KeystoreCompat](android-keystore-compat/readme.md) 				| [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion) |
| KeystoreCompat L+      		| [KeystoreCompat-elPlus](android-keystore-compat-elplus/readme.md) | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat-elplus/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat-elplus/_latestVersion) |
| KeystoreCompat M+				| [KeystoreCompat-emPlus](android-keystore-compat-emplus/readme.md) | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat-emplus/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat-emplus/_latestVersion) |


<br/>

![KeystoreCompat princip](./android-keystore-compat/extras/diagram/KeystoreCompat.png "KeystoreCompat princip")

<br/><br/>

[ ![STRV](./extras/strv-talk/STRV-Black_small.png) ](https://www.strv.com/)


* [STRV Keystore compat mini-talk google slides](https://docs.google.com/presentation/d/1KVMmK59jRQSequ_Ib157ZttFor9k14qZ3938IPxOAKg/edit?usp=sharing)
* [STRV Keystore compat mini-talk pdf](./extras/strv-talk/Android-Keystore-handling.pdf)

## Encrypted Realm ##

For sample implementation of the Realm persistence encryption look at [DB Showcase repository](https://github.com/kotomisak/db-showcase-android).  
Related article describing this encryption is available on Medium [Encrypted Realm & Android Keystore](https://medium.com/@strv/encrypted-realm-android-keystore-d4f0915905e9)

## Rooted device detection ##

Very lightweight but powerful solution is using [RootBear library](https://github.com/scottyab/rootbeer) which is also used by [KeystoreCompat](android-keystore-compat/readme.md) library.  
Another solution is to use robust google solution [SafetyNet](https://developer.android.com/training/safetynet/index.html) (used for example by AndroidPay solution). 
SafetyNet is complex online/offline device checking solution, but it requires Google Play Services dependency.

## Json Web Token ##

SecurityShowcase backend/client is using [Json Web Token (JWT)](https://jwt.io/) to wrap all authentization payload. 
JWT [RFC 7519](https://tools.ietf.org/html/rfc7519) is in fact base64 string (easy to transfer in header) containing lot of information and is readable by anyone.
JWT is signed by the server, so server can verify JWT (returning from the client) to be not malformed.  

How does the process works?

LOGIN PHASE / AUTHENTICATION
* client/app sends credentials (username/password) and requests authentication token (JWT)
* server creates JWT (based on correct credentials) and sign this token with the secret. JWT also contains additional information related to authentized user.
* client/app obtain proper JWT containing all information about the user and will store this token for the future use. 

SECURED REQUESTS / AUTHORIZATION
* client/app bundle JWT to header of any secured request
* server verify obtained JWT against original secret (to be not malformed) and then use any information (token validity, users role, users id ...) in the token to 
authorize user for current operation.
