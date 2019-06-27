package cz.koto.securityshowcase.core.arch

//import dagger.android.support.AndroidSupportInjection
//import javax.inject.Inject
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber

abstract class BaseFragmentLegacy : Fragment(), BaseUIScreenLegacy {

	companion object {
		const val SNACK_BAR_MAX_LINES_DEFAULT = 4
	}

//	@Inject
//	lateinit var appInterface: AppInterface

	override val baseActivity: BaseActivity
		get() = activity as? BaseActivity ?: throw IllegalStateException("No activity in this fragment, can't finish")

	override fun getContext(): Context = super.getContext()!!

	override var lastSnackbar: Snackbar? = null

	override fun onAttach(context: Context) {
		//CrashlyticsUtility.setCurrentFragmentKey(javaClass.simpleName)
		Timber.v(javaClass.simpleName)
		super.onAttach(context)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		Timber.v(javaClass.simpleName)
		AndroidSupportInjection.inject(this)
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		Timber.v(javaClass.simpleName)
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		Timber.v(javaClass.simpleName)
		super.onViewCreated(view, savedInstanceState)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		Timber.v(javaClass.simpleName)
		super.onActivityCreated(savedInstanceState)
	}

	override fun onStart() {
		Timber.v(javaClass.simpleName)
		super.onStart()
	}

	override fun onResume() {
		//CrashlyticsUtility.setCurrentFragmentKey(javaClass.simpleName)
		Timber.v(javaClass.simpleName)
		super.onResume()
	}

	override fun onPause() {
		Timber.v(javaClass.simpleName)
		super.onPause()
	}

	override fun onStop() {
		Timber.v(javaClass.simpleName)
		super.onStop()
	}

	override fun onDestroyView() {
		Timber.v(javaClass.simpleName)
		lastSnackbar?.dismiss()
		lastSnackbar = null
		super.onDestroyView()
	}

	override fun onDestroy() {
		Timber.v(javaClass.simpleName)
		super.onDestroy()
	}

	override fun onDetach() {
		Timber.v(javaClass.simpleName)
		super.onDetach()
	}

	override fun getExtras(): Bundle? = activity?.intent?.extras

	/**
	 * Serves only as shortcut to activity.finish()
	 * WARNING: has to be final, otherwise children fragments could override and think that the code will be called on finish
	 */
	final override fun finish() = super.finish()

	/**
	 * Custom method which provides onBackPressed functionality for fragments.
	 * It's actually handled in activity, which tries to find last fragment on stack of fragments.
	 * @return whether fragment consumed the back click
	 */
	open fun onBackPressed(): Boolean {
		val lastFragment = childFragmentManager.fragments.lastOrNull { it.isResumed }
		if (lastFragment != null && lastFragment is BaseFragmentLegacy && lastFragment.onBackPressed()) {
			return true
		}
		return false
	}
}