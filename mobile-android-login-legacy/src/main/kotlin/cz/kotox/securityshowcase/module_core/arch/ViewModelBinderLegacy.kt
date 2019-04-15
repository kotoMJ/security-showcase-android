package cz.kotox.securityshowcase.module_core.arch

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import cz.kotox.securityshowcase.module_core.arch.event.Event
import cz.kotox.securityshowcase.module_core.arch.event.EventObserver

interface ViewModelBinder<V : BaseViewModelLegacy, B : ViewDataBinding> : LifecycleOwner, BaseUIScreenLegacy {
	var binding: B
	var viewModel: V
	val currentFragmentManager: FragmentManager
	fun setupViewModel(): V
	fun inflateBindingLayout(inflater: LayoutInflater): B
	fun getViewLifecycleOwner(): LifecycleOwner
}

inline fun <reified T : Event> ViewModelBinder<*, *>.observeEvent(crossinline action: (T) -> Unit) {
	viewModel.observeEvent(getViewLifecycleOwner(), T::class.java, eventObserver = object : EventObserver<T> {
		override fun onChanged(event: T?) = action.invoke(event!!) //TODO MJ really this assert? !!
	})
}