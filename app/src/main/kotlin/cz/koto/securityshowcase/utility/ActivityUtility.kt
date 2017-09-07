package cz.koto.securityshowcase.utility

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager


inline fun <reified A : Activity> Activity.start() = startActivity(Intent(this, A::class.java))

inline fun <reified A : Activity> Activity.start(config: Intent.() -> Unit) =
		startActivity(Intent(this, A::class.java).apply(config))

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.statusBarColor(@ColorRes color: Int) = runOnLollipop {
	window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
	window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
	window.statusBarColor = ContextCompat.getColor(this, color)
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setTransparentStatusBarColor(@ColorInt color: Int) = runOnLollipop {
	window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	window.statusBarColor = color
}

fun Activity.landscape() {
	requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Activity.portrait() {
	requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}