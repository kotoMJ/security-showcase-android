package cz.kotox.securityshowcase.module_core.arch

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import javax.inject.Inject

abstract class BaseFragmentViewModelLegacy<V : BaseViewModelLegacy, B : ViewDataBinding> : BaseFragmentLegacy(), ViewModelBinder<V, B> {

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory
	override lateinit var viewModel: V
	override lateinit var binding: B
	override val currentFragmentManager: FragmentManager get() = requireFragmentManager()

	companion object {
		private const val LOADING_DIALOG_PROGRESS_TAG = "LOADING_DIALOG_PROGRESS_TAG"
	}

	inline fun <reified VM : V> findViewModel(ofLifecycleOwner: Fragment = this, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(VM::class.java)
	inline fun <reified VM : V> findViewModel(ofLifecycleOwner: FragmentActivity, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(VM::class.java)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = setupViewModel()
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		binding = inflateBindingLayout(inflater)
		return binding.root
	}
}
