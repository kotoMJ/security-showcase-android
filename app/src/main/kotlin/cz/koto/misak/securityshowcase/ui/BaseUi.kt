package cz.koto.misak.securityshowcase.ui

import android.databinding.ViewDataBinding
import android.support.annotation.DrawableRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import cz.kinst.jakub.viewmodelbinding.ViewModel
import cz.kinst.jakub.viewmodelbinding.ViewModelActivity
import cz.kinst.jakub.viewmodelbinding.ViewModelFragment
import cz.koto.misak.securityshowcase.ContextProvider
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.model.SecurityShowcaseAPIError
import cz.koto.misak.securityshowcase.utility.inputMethodManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.toolbar.view.*
import org.parceler.Parcels

abstract class BaseViewModel<T : ViewDataBinding> : ViewModel<T>() {

    val detached = PublishSubject.create<Unit>()

    protected fun <A> bind(source: Observable<A>, action: (A) -> Unit) = source
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(detached)
            .subscribe(action)

    override fun onViewModelDestroyed() {
        detached.onNext(Unit)
        super.onViewModelDestroyed()
    }

    override fun getActivity(): BaseActivity<*, *> {
        return super.getActivity() as BaseActivity<*, *>
    }

    fun getToolbar() = binding.root.toolbar
}

inline fun <reified T> BaseViewModel<*>.unwrap(key: String): T =
        Parcels.unwrap<T>(view.bundle.getParcelable(key))

inline fun <reified T> BaseViewModel<*>.unwrapSafe(key: String, action: (T) -> Unit): Unit =
        try {
            action(Parcels.unwrap<T>(view.bundle.getParcelable(key)))
        } catch (e: Exception) {
            e.printStackTrace()
        }

abstract class BaseFragment<T : ViewDataBinding, S : BaseViewModel<T>> : ViewModelFragment<T, S>() {

    fun showSnackBar(text: String, infinite: Boolean) =
            (activity as BaseActivity<T, S>).showSnackBar(text, infinite)

    fun handlesBackButton() = false

    fun onBackButtonPressed() {}
}

abstract class BaseActivity<T : ViewDataBinding, S : BaseViewModel<T>> : ViewModelActivity<T, S>() {

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed(); true
        }
        else -> super.onOptionsItemSelected(item)
    }


    fun showSnackBar(text: String, infinite: Boolean = false) {
        Snackbar.make(binding.root, text, if (infinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT).show()
    }


    fun hideSoftKeyboard() =
            inputMethodManager
                    .hideSoftInputFromWindow((activity.currentFocus ?: View(context)).windowToken, 0)


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
        val fm = (activity as AppCompatActivity).supportFragmentManager
        val currentFragment = fm.findFragmentById(R.id.container)
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.container, fragment)
        if (currentFragment != null && addToBackStack)
            transaction.addToBackStack(fragment.javaClass.name)
        transaction.commitAllowingStateLoss()
    }

    fun clearBackStack() {
        try {
            (activity as AppCompatActivity).supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } catch (ignored: IllegalStateException) {
            ignored.printStackTrace()
        }
    }

    fun showErrorDialog(throwable: Throwable, callback: () -> Unit) {

        val apiError: SecurityShowcaseAPIError = SecurityShowcaseRetrofitProvider.convertRetrofitThrowable(throwable)

        /**
         * Example errors from current API:
         * Http 422[when order date is in the past] "message": "Payment cannot be in the past."
         * Http 400[when order amount is negative]  "message": "amount"
         */
        if (apiError.statusCode == 400) {
            apiError.title = ContextProvider.getString(R.string.error_wrong_parameter)
        }
        AlertDialog.Builder(context)
                .setTitle(apiError.title)
                .setMessage(apiError.message)
                .setPositiveButton(android.R.string.ok, { dialog, which ->
                    callback()
                })
                .show()
    }
}