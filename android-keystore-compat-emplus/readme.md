# KeystoreCompat M+


[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat-emplus/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat-emplus/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


```
If your application supports API lower than 23, include this line to app's AndroidManifest file:
  
<uses-sdk tools:overrideLibrary="cz.koto.keystorecompat" />

```

This is lite version of [KeystoreCompat](../android-keystore-compat/readme.md). 
`KeystoreCompat M+ offers the same functionality like KeystoreCompat`, but with minimal API version 23 (M).
Use this variant in case you don't want to have bundled legacy code under API 23. 

## Install ##

Keystore compat requires to add `cz.koto:android-keystore-compat-emplus`.

Current version of the library require to also install all necessary impl modules.

```groovy
dependencies {
	api("cz.koto:android-keystore-compat-emplus:2.0.1") {
		exclude group: 'com.android.support'
	}
	api("cz.koto:android-keystore-compat-base:2.0.1")
	api("cz.koto:android-keystore-compat-23:2.0.1")
	implementation('com.scottyab:rootbeer-lib:0.0.6') {
		exclude group: 'com.android.support'
	}
}
```

## Licence ##
The Apache Software License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.txt

