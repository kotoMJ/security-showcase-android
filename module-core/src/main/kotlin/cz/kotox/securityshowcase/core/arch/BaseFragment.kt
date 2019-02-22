package cz.kotox.securityshowcase.core.arch

import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import cz.kotox.securityshowcase.core.R
import cz.kotox.securityshowcase.core.di.Injectable
import javax.inject.Inject

abstract class BaseFragment : Fragment(), Injectable {

	companion object {
		const val SNACK_BAR_MAX_LINES_DEFAULT = 4
	}

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	var lastSnackbar: Snackbar? = null

	inline fun <reified VM : ViewModel> findViewModel(viewModel: Class<VM>, ofLifecycleOwner: Fragment = this, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(viewModel)

	fun showToast(message: String, withOffset: Boolean = true) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).apply {
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