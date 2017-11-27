package cz.koto.securityshowcase.ui

import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import cz.koto.securityshowcase.ContextProvider
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.api.rest.RetrofitAuthProvider
import cz.koto.securityshowcase.model.SecurityShowcaseAPIError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject


open class BaseArchActivity : AppCompatActivity() {

	val detached = PublishSubject.create<Unit>()


	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		android.R.id.home -> {
			onBackPressed(); true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onDestroy() {
		detached.onNext(Unit)
		super.onDestroy()
	}

	protected fun <A> bind(source: io.reactivex.Observable<A>, action: (A) -> Unit) = source
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.takeUntil(detached)
			.subscribe(action)

	//TODO solve this different way
//	fun showSnackBar(text: String, infinite: Boolean = false) {
//		Snackbar.make(binding.root, text, if (infinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT).show()
//	}
//
//
//	fun hideSoftKeyboard() =
//			inputMethodManager
//					.hideSoftInputFromWindow((activity.currentFocus ?: View(context)).windowToken, 0)
//

	fun setupToolbar(toolbar: Toolbar, title: String, subtitle: String? = null, @DrawableRes icon: Int? = null, showBack: Boolean = false) {
		setSupportActionBar(toolbar)
		supportActionBar?.run {
			setTitle(title)
			if (subtitle != null) setSubtitle(subtitle)
			if (icon != null) setHomeAsUpIndicator(icon)
			setDisplayHomeAsUpEnabled(showBack)
		}
	}

	fun setToolbarTitle(title: String, showBack: Boolean = false) {
		showToolbar()
		supportActionBar?.run {
			setTitle(title)
			setDisplayHomeAsUpEnabled(showBack)
		}
	}

	fun showToolbar(show: Boolean = true) {
		if (show)
			supportActionBar?.show()
		else
			supportActionBar?.hide()
	}

	fun switchToFragment(fragment: Fragment, addToBackStack: Boolean = false) {
		val fm = this.supportFragmentManager
		val currentFragment = fm.findFragmentById(R.id.container)
		val transaction = fm.beginTransaction()
		transaction.replace(R.id.container, fragment)
		if (currentFragment != null && addToBackStack)
			transaction.addToBackStack(fragment.javaClass.name)
		transaction.commitAllowingStateLoss()
	}

	fun clearBackStack() {
		try {
			this.supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
		} catch (ignored: IllegalStateException) {
			ignored.printStackTrace()
		}
	}

	fun showErrorDialog(throwable: Throwable, callback: () -> Unit) {

		val apiError: SecurityShowcaseAPIError = RetrofitAuthProvider().convertRetrofitThrowable(throwable)

		/**
		 * Example errors from current API:
		 * Http 422[when order date is in the past] "message": "Payment cannot be in the past."
		 * Http 400[when order amount is negative]  "message": "amount"
		 */
		if (apiError.statusCode == 400) {
			apiError.title = ContextProvider.getString(R.string.error_wrong_parameter)
		}
		AlertDialog.Builder(applicationContext)
				.setTitle(apiError.title)
				.setMessage(apiError.message)
				.setPositiveButton(android.R.string.ok, { dialog, which ->
					callback()
				})
				.show()
	}
}

