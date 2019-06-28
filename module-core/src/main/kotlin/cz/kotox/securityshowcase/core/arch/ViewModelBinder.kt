package cz.kotox.securityshowcase.core.arch

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import cz.kotox.securityshowcase.core.arch.event.Event
import cz.kotox.securityshowcase.core.arch.event.EventObserver

interface ViewModelBinder<V : BaseViewModel, B : ViewDataBinding> : LifecycleOwner, BaseUIScreen {
	var binding: B
	var viewModel: V
	val currentFragmentManager: FragmentManager
	fun setupViewModel(): V
	fun inflateBindingLayout(inflater: LayoutInflater): B
	fun getViewLifecycleOwner(): LifecycleOwner
}

inline fun <reified T : Event> ViewModelBinder<*, *>.observeEvent(crossinline action: (T) -> Unit) {
	viewModel.observeEvent(getViewLifecycleOwner(), T::class.java, eventObserver = object : EventObserver<T> {
		override fun onChanged(event: T) = action.invoke(event)
	})
}
