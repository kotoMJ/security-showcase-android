package cz.kotox.securityshowcase.module_core.arch

import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cz.kotox.securityshowcase.R
import cz.kotox.securityshowcase.module_core.arch.BaseFragmentLegacy.Companion.SNACK_BAR_MAX_LINES_DEFAULT

interface BaseUIScreenLegacy {
	val baseActivity: BaseActivity
	fun getResources(): Resources
	fun getExtras(): Bundle? = baseActivity.intent.extras
	fun finish() = baseActivity.finish()
	var lastSnackbar: Snackbar?

	fun showToast(@StringRes stringRes: Int, withOffset: Boolean = true) {
		showToast(baseActivity.getString(stringRes), withOffset)
	}

	fun showToast(message: String, withOffset: Boolean = true) {
		Toast.makeText(baseActivity, message, Toast.LENGTH_LONG).apply {
			if (withOffset) {
				setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 2 * getResources().getDimensionPixelOffset(R.dimen.global_bottom_bar_height))
			}
		}.show()
	}

	fun showSnackbar(view: View, @StringRes stringRes: Int, length: Int = Snackbar.LENGTH_LONG, maxLines: Int = SNACK_BAR_MAX_LINES_DEFAULT, config: (Snackbar.() -> Unit)? = null) {
		showSnackbar(view, view.context.getString(stringRes), length, maxLines, config)
	}

	fun showSnackbar(view: View, message: String, length: Int = Snackbar.LENGTH_LONG, maxLines: Int = SNACK_BAR_MAX_LINES_DEFAULT, config: (Snackbar.() -> Unit)? = null) {
		val newSnackbar = Snackbar.make(view, message, length).apply { config?.invoke(this) }
		newSnackbar.view.findViewById<TextView>(R.id.snackbar_text)?.maxLines = maxLines
		lastSnackbar?.dismiss()
		lastSnackbar = newSnackbar
		newSnackbar.show()
	}
}