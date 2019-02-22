package cz.kotox.securityshowcase.core.arch

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

abstract class ObservableViewModel : ViewModel(), Observable {

	@Transient
	private val observableCallbacks: PropertyChangeRegistry = PropertyChangeRegistry()

	@Synchronized
	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		observableCallbacks.add(callback)
	}

	@Synchronized
	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		observableCallbacks.remove(callback)
	}

}