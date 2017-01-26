package cz.koto.misak.keystorecompat

import android.content.Intent

class ForceLockScreenKitKatException : RuntimeException {

    var lockIntent: Intent

    constructor(lockIntent: Intent) {
        this.lockIntent = lockIntent
    }
}
