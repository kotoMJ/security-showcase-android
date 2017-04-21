# Security showcase #

![DbShowcase](./app/src/main/res/mipmap-hdpi/ic_launcher.png "DbShowcase") <a href="https://play.google.com/store/apps/details?id=cz.koto.misak.securityshowcase"><img src="./extras/banner/google-play-badge.png" height="72"/></a>

[![Build Status](https://travis-ci.org/kotomisak/security-showcase-android.svg)](https://travis-ci.org/kotomisak/security-showcase-android)

This is sample application pointing some security related practices on Android device.

## Android keystore ##

SecurityShowcase application contains example of using standard Android Security with the Keystore.
All Android Keystore related stuff is bundled in KeystoreCompat library (available in this source code).

KeystoreCompat library should help to prevent pain when starting work with keystore from the official documentation
or StackOverflow discussion. **The main point of this library is to provide the same services for all backward** (...Compat) **supported API versions (19+).**
The backward support is something, what all available libraries and blog posts lacks!

Read more about [KeystoreCompat](android-keystore-compat/readme.md)<br/>
[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto.misak/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto.misak/android-keystore-compat/_latestVersion)<br/><br/>
[ ![STRV](./extras/strv-talk/STRV-Black_small.png) ](https://www.strv.com/)


* [STRV Keystore compat mini-talk google slides](https://docs.google.com/presentation/d/1KVMmK59jRQSequ_Ib157ZttFor9k14qZ3938IPxOAKg/edit?usp=sharing)
* [STRV Keystore compat mini-talk pdf](./extras/strv-talk/Android-Keystore-handling.pdf)
* [Encrypted Realm & Android Keystore](https://medium.com/@strv/encrypted-realm-android-keystore-d4f0915905e9)


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

## GraphQL ##

### Apollo client ###

#### Download proper schema ####
Ensure you have already installed `apollo-codegen`. If not install it by using `npm install apollo-codegen`  
Generate `schema.json` using this command `apollo-codegen download-schema http://kotopeky.cz/graphql --output schema.json`
#### Download proper schema ####


## SecurityShowcase gradle notes
**Build sample app**

 * `./gradlew assembleRostiRelease`

**Dependency diagnostic**

  * `./gradlew dependencyReport --configuration compile`<br/>
  * `./gradlew dependencyInsighty --configuration compile --dependency com.android.support:appcompat-v7`<br/>
  * `./gradlew dependencyInsighty --configuration compile --dependency org.jetbrains.kotlin:kotlin-stdlib`<br/>
