# KeystoreCompat


[ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<br/><br/>
Save secretto local shared preferences using Android default security and Android Keystore.
Ensures handling LockScreen and compatibility among diversity of Android versions.

KeystoreCompat also keeps in mind existing Keystore-related vulnerabilities
and therefore follow the minimum API/rooted device detection and also inform about existing caveats/enhancements in all supported API version.

![KeystoreCompat princip](./extras/diagram/KeystoreCompat.png "KeystoreCompat princip")

## UseCase ##

Does your app use classic credentials (e.g. username & password / JWT / hash) to connect to secured part of the API OR encrypted database?


**Want to let user access your application using just Android default security**
(PIN/password/gesture/fingerprint) and do not force let user type username/password again and again?

**Or want to let application use keystore without using lockScreen?**
  
**If so, this library is designed for you!**

Sample application available on Github (also distributed via Google Play)
<br/> * [App login security - kotlin project](https://github.com/kotomisak/security-showcase-android/tree/develop/app)


## Install ##
### Install KeystoreCompat below API 19
If your application supports API lower than 19, include this line to app's AndroidManifest file:
```xml
<uses-sdk tools:overrideLibrary="cz.koto.keystorecompat" />
```

### Install KeystoreCompat min. API 19
| KeystoreCompat variant        | JCenter 											 |
| ----------------------------- | -------------------------------------------------- |
| KeystoreCompat      		    | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat/_latestVersion) |

```groovy
dependencies {
	api("cz.koto:android-keystore-compat:2.0.3") {
		exclude group: 'com.android.support'
	}
	api("cz.koto:android-keystore-compat-base:2.0.3")
	api("cz.koto:android-keystore-compat-19:2.0.1")
	api("cz.koto:android-keystore-compat-21:2.0.1")
	api("cz.koto:android-keystore-compat-23:2.0.1")
	implementation('com.scottyab:rootbeer-lib:0.0.6') {
		exclude group: 'com.android.support'
	}
}
```

### Install KeystoreCompat min. API 21
| KeystoreCompat variant        | Readme  															| JCenter 											 |
| ----------------------------- | ----------------------------------------------------------------- | -------------------------------------------------- |
| KeystoreCompat L+      		| [KeystoreCompat-elPlus](../android-keystore-compat-elplus/readme.md) | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat-elplus/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat-elplus/_latestVersion) |

```groovy
dependencies {
	api("cz.koto:android-keystore-compat-elpuls:2.0.3") {
		exclude group: 'com.android.support'
	}
	api("cz.koto:android-keystore-compat-base:2.0.3")
	api("cz.koto:android-keystore-compat-21:2.0.1")
	api("cz.koto:android-keystore-compat-23:2.0.1")
	implementation('com.scottyab:rootbeer-lib:0.0.6') {
		exclude group: 'com.android.support'
	}
}
```

### Install KeystoreCompat min. API 23
| KeystoreCompat variant        | Readme  															| JCenter 											 |
| ----------------------------- | ----------------------------------------------------------------- | -------------------------------------------------- |
| KeystoreCompat M+				| [KeystoreCompat-emPlus](../android-keystore-compat-emplus/readme.md) | [ ![Download](https://api.bintray.com/packages/kotomisak/cz.koto/android-keystore-compat-emplus/images/download.svg) ](https://bintray.com/kotomisak/cz.koto/android-keystore-compat-emplus/_latestVersion) |

```groovy
dependencies {
	api("cz.koto:android-keystore-compat-empuls:2.0.3") {
		exclude group: 'com.android.support'
	}
	api("cz.koto:android-keystore-compat-base:2.0.3")
	api("cz.koto:android-keystore-compat-23:2.0.1")
	implementation('com.scottyab:rootbeer-lib:0.0.6') {
		exclude group: 'com.android.support'
	}
}
```

## Usage ##
### 1. `Optionally` define your own config over default KeystoreCompatConfig
```kotlin
class YourKeystoreCompatConfig : KeystoreCompatConfig() {

	/**
	 * How many cancellation is necessary to suppress AndroidLoginScreen / KitkatAdminRequestDialog .
	 */
	override open fun getDialogDismissThreshold(): Int {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			return 1 //In case of Admin request dialog on KitKat escape after first cancel click.
		} else {
			return 2 //In case of standard Android security dialog dismiss dialog after second CANCEL button click
		}
	}

	override fun isRootDetectionEnabled(): Boolean {
		if (BuildConfig.DEBUG) {
			return false
		} else
			return super.isRootDetectionEnabled()
	}
}
```

### 2. `Optionally` override in strings.xml resources
```xml
<resources>
	<!-- Override KeystoreCompat default string -->
	<string name="kc_lock_screen_title">SecurityShowcase LockScreen</string>
	<string name="kc_lock_screen_description">Use Android security to open SecurityShowcase</string>
</resources>
```

### 3. KeystoreCopmpat instance

KeystoreCompat since 2.0 is not static anymore, so firstly get instance to work with.
```kotlin
    import cz.koto.keystorecompat.KeystoreCompat
    
	open class YourApplication : Application() {

	lateinit var keystoreCompat: KeystoreCompat

	override fun onCreate() {
		super.onCreate()
		//Other usual other inits here...
		
		keystoreCompat = KeystoreCompat.getInstance(this, YourKeystoreCompatConfig())
	}
}
```

### 4. Example of in memory object for credentials handling

Example of in memory credential storage object. This object is managed further in this example.

```kotlin
object CredentialStorage {

	private var accessToken: String? = null
	private var userName: String? = null
	private var password: String? = null

	var forceLockScreenFlag: Boolean? = true

	fun getAccessToken(): String? {
		if (accessToken != null)
			Logcat.d("getToken %s", accessToken!!)
		else
			Logcat.d("NULL token!")
		return accessToken
	}

	fun getUserName() = userName
	fun getPassword() = password

	fun storeUser(token: String, username: String, pass: String) {
		accessToken = token
		userName = username
		password = pass
	}

	fun performLogout() {
		accessToken = null
		userName = null
		password = null
	}

	/**
	 * Set forceLockScreenFlag to avoid automatic login just after logout.
	 */
	fun forceLockScreenFlag() {
		forceLockScreenFlag = true
	}

	/**
	 * Dismiss requirement to display LockScreen given by application.
	 * Requirement given by certificate definition remains.
	 */
	fun dismissForceLockScreenFlag() {
		this.forceLockScreenFlag = false
	}

}
```


### 5. On login page

Here is how could be KeystoreCompat handled on login page.

```kotlin
import cz.koto.keystorecompat.base.exception.ForceLockScreenKitKatException
import cz.koto.keystorecompat.base.utility.forceAndroidAuth
import cz.koto.keystorecompat.base.utility.runSinceKitKat

class LoginActivity : AppCompatActivity() {

	companion object {
		val FORCE_SIGNUP_REQUEST = 1001
	}
	
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Other usual other on create activities here...
        
        onLoginDisplayed(true)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FORCE_SIGNUP_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                (application as SecurityApplication).keystoreCompat.increaseLockScreenCancel()
                this.finish()
            } else {
                onLoginDisplayed(false)
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }
    
    fun onLoginDisplayed(firstAttachment: Boolean) {
        runSinceKitKat {
        
            if (keystoreCompat.hasSecretLoadable()) {
                keystoreCompat.loadSecretAsString(
                    //Decrypt credentials from loaded secret and use them for sign in
					{ decryptResult ->
					    //email/password were saved together separated by semicolon
						decryptResult.split(';').let {
							viewModel?.email?.set(it[0])
							viewModel?.password?.set(it[1])
							viewModel?.signIn()
						}
					},
					{ exception ->
					    ///Disable flag for requirement to display LockScreen given by application.
						CredentialStorage.dismissForceLockScreenFlag()
						
						if (exception is ForceLockScreenKitKatException) {
							this.startActivityForResult(exception.lockIntent, FORCE_SIGNUP_REQUEST)
						} else {
						
						    //Cleanup creadentials from in memory object
							CredentialStorage.performLogout()
							
						    forceAndroidAuth(getString(R.string.kc_lock_screen_title),
								getString(R.string.kc_lock_screen_description),
								{ intent -> this.startActivityForResult(intent, FORCE_SIGNUP_REQUEST) },
								keystoreCompat.context
							)
						}
					},
					//Get flag for requirement to display LockScreen to avoid automatic login just after logout.
					CredentialStorage.forceLockScreenFlag
				)
            } else {
                // Use standard login without 
            }
        }
    }   
}
```

```kotlin
class LoginViewModel(context: Application) : AndroidViewModel(context) {
	init {
	     //Enable flag for requirement to display LockScreen given by application.
		CredentialStorage.forceLockScreenFlag()
	}

	private fun onSuccessfulLogin(token: String?) =
			if (isValidJWT(token)) {
			    //Store credentials to in memory object
				CredentialStorage.storeUser(token!!, email.get() ?: "", password.get() ?: "")
			}
}
```


### 5. Logged successfully


Here is how could be KeystoreCompat handled when you enter secured area.

```kotlin
class YourSecretAreaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //activate eventually dismissed KeystoreCompat LockScreen feature.
        keystoreCompat.lockScreenSuccessful()
    }
    
    
    fun onLogout(){
         //Enable flag for requirement to display LockScreen given by application.
        CredentialStorage.forceLockScreenFlag()
        //Cleanup creadentials from in memory object
        CredentialStorage.performLogout()
        
        //Go to login activity
        start<LoginActivity>()
        finish()
        
    }
}
```

### 6. Example of credentials enrollment (save/delete credentials securely for future usage)

Here is how could be KeystoreCompat handled on some settings/enrollment page.

```kotlin
import cz.koto.keystorecompat.base.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat.base.utility.forceAndroidAuth
import cz.koto.keystorecompat.base.utility.runSinceKitKat

class YourEnrollmentFragment() {

    private fun storeSecret() {
        keystoreCompat.clearCredentials()
        keystoreCompat.storeSecret(
                "${CredentialStorage.getUserName()};${CredentialStorage.getPassword()}",
                {
                    Logcat.e("Store credentials failed!", it)
                    if (it is ForceLockScreenMarshmallowException) {
                        forceAndroidAuth(
                            getString(R.string.kc_lock_screen_title), 
                            getString(R.string.kc_lock_screen_description),
                            { intent -> activity?.startActivityForResult(intent, MainActivity.FORCE_ENCRYPTION_REQUEST_M) }, 
                            keystoreCompat.context)
                    }
                },
                { Logcat.d("Credentials stored.") }) 
    }
    
    //Connect enrollment checkbox this way
    override fun onCheckedChanged(checked: Boolean) {
        runSinceKitKat {
            if (checked) {
                viewModel.androidSecuritySelectable.set(false)
                storeSecret()
            } else {
                keystoreCompat.deactivate()
            }
        }
	}

}
```
#### 6.1 API 19 APP/GOOGLE PLAY DISCLAIMER!
For API 19 don't forget to warn user, that enrollment require DEVICE ADMIN RIGHT for lock screen!
`KestoreCompat.deactivate` then ensures to deactivate this admin right too.

![DEVICE_ADMIN_RIGHTS](../extras/screens/scr_DEVICE_ADMIN_RIGHTS)

Don't forget to mention this also in Full description of the app in the Google Play.
Your app would be removed from store otherwise.

```
This app uses the Device Administrator permission for the Android version KitKat (API 4.4.4).  
Lower versions doesn't support security mode. 
Higher versions doesn't need Device Administrator permissions to be secure.
```

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

