package cz.koto.keystorecompat_base.exception

import android.content.Intent

class ForceLockScreenKitKatException : KeystoreCompatException {

	var lockIntent: Intent

	constructor(lockIntent: Intent) {
		this.lockIntent = lockIntent
	}
}
