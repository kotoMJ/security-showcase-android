package com.strv.keystorecompat

import android.content.Intent

class ForceLockScreenKitKatException : RuntimeException {

    var lockIntent: Intent

    constructor(lockIntent: Intent) {
        this.lockIntent = lockIntent
    }
}
