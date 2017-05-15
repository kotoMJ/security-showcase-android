# Security showcase #

![DbShowcase](./app/src/main/res/mipmap-hdpi/ic_launcher.png "DbShowcase") <a href="https://play.google.com/store/apps/details?id=cz.koto.misak.securityshowcase"><img src="./extras/banner/google-play-badge.png" height="72"/></a>  
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/kotomisak/security-showcase-android.svg)](https://travis-ci.org/kotomisak/security-showcase-android)

This is sample application pointing some security related practices on Android device.  
Complete server-side endpoint implementation for this project is available on GitHub [KoTiNode](https://github.com/kotomisak/kotinode).

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

## Encrypted Realm ##

For sample implementation of the Realm persistence encryption look at [DB Showcase repository](https://github.com/kotomisak/db-showcase-android).  
Related article describing this encryption is available on Medium [Encrypted Realm & Android Keystore](https://medium.com/@strv/encrypted-realm-android-keystore-d4f0915905e9)

## Rooted device detection ##

Very lightweight but powerful solution is using [RootBear library](https://github.com/scottyab/rootbeer) which is also used by [KeystoreCompat](android-keystore-compat/readme.md) library.  
Another solution is to use robust google solution [SafetyNet](https://developer.android.com/training/safetynet/index.html) (used for example by AndroidPay solution). 
SafetyNet is complex online/offline security solution, but it require Google Play Services dependency.

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
Showcase contains sample implementation of communication with the graphql endpoint (besides the REST).

### Apollo client ###
[Official Apollo Android doc](https://github.com/apollographql/apollo-android)
#### Graph schema ####
`schema.json` definition file is not composed manually, but is always completely downloaded from the server.
Before download just ensure you have already installed `apollo-codegen`. If not install it by using `npm install apollo-codegen`  
Generate `schema.json` using this command `apollo-codegen download-schema http://kotopeky.cz/graphql --output schema.json`

#### GraphiQL ####
GraphiQL console allows to try call query manually, let's try it here:  

[Login GraphiQL](https://kotopeky.cz/graphiql/?query=query%20login(%24password%3A%20String!%2C%20%24email%3A%20String!)%20%7B%0A%20login(password%3A%24password%2C%20email%3A%24email)%20%7B%0A%20%20%20token%0A%20%20%20errorMessage%0A%20%7D%0A%7D%0A&variables=%7B%0A%20%20%22password%22%3A%20%22showcase1234%22%2C%0A%20%20%22email%22%3A%20%22security%40showcase.cz%22%0A%7D&operationName=login)


#### About GraphQl on the web ####
 [02/2017 Using Apollo client for Android 0.1.0](http://engineering.dailymotion.com/using-the-apollo-graphql-client-for-android/)  
 [04/2017 Using Apollo client for Android 0.3.0 ](https://stackoverflow.com/questions/43304986/using-the-apollo-graphql-client-for-android/43306056)	  

## SecurityShowcase gradle notes
**Build sample app**

 * `./gradlew assembleRostiRelease`

**Dependency diagnostic**

  * `./gradlew dependencyReport --configuration compile`<br/>
  * `./gradlew dependencyInsighty --configuration compile --dependency com.android.support:appcompat-v7`<br/>
  * `./gradlew dependencyInsighty --configuration compile --dependency org.jetbrains.kotlin:kotlin-stdlib`<br/>
