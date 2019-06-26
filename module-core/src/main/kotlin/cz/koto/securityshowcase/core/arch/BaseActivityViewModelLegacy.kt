package cz.kotox.securityshowcase.module_core.arch

import android.os.Bundle
import javax.inject.Inject

abstract class BaseActivityViewModelLegacy<V : BaseViewModelLegacy, B : ViewDataBinding> : BaseActivity(), ViewModelBinder<V, B> {
	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory
	override lateinit var viewModel: V
	override lateinit var binding: B
	override val currentFragmentManager: FragmentManager get() = supportFragmentManager
	override fun getViewLifecycleOwner(): LifecycleOwner = this

	inline fun <reified VM : V> findViewModel(ofLifecycleOwner: FragmentActivity = this, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(VM::class.java)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = setupViewModel()
		binding = inflateBindingLayout(layoutInflater)
		setContentView(binding.root)
	}

	override fun finish() {
		super<BaseActivity>.finish()
	}
}