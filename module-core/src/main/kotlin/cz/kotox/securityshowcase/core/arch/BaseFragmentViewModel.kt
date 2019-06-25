package cz.kotox.securityshowcase.core.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Inject

abstract class BaseFragmentViewModel<V : BaseViewModel, B : ViewDataBinding> : BaseFragment(), ViewModelBinder<V, B> {

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
