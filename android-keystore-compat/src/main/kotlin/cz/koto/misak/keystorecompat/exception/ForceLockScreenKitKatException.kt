package cz.koto.misak.keystorecompat.exception

import android.content.Intent

class ForceLockScreenKitKatException : KeystoreCompatException {

    var lockIntent: Intent

    constructor(lockIntent: Intent) {
        this.lockIntent = lockIntent
    }
}
