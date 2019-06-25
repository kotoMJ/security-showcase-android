package cz.kotox.securityshowcase.core.arch

import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import cz.kotox.securityshowcase.core.arch.event.Event
import cz.kotox.securityshowcase.core.arch.event.EventBus
import cz.kotox.securityshowcase.core.arch.event.EventObserver

abstract class BaseViewModel : Observable, ViewModel() {
	@Transient
	private var mObservableCallbacks: PropertyChangeRegistry? = null
	private var eventBus = EventBus()

	@Synchronized
	override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
		if (mObservableCallbacks == null) {
			mObservableCallbacks = PropertyChangeRegistry()
		}
		mObservableCallbacks?.add(callback)
	}

	@Synchronized
	override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
		if (mObservableCallbacks != null) {
			mObservableCallbacks?.remove(callback)
		}
	}

	@Synchronized
	fun notifyChange() {
		if (mObservableCallbacks != null) {
			mObservableCallbacks?.notifyCallbacks(this, 0, null)
		}
	}

	fun notifyPropertyChanged(fieldId: Int) {
		if (mObservableCallbacks != null) {
			mObservableCallbacks?.notifyCallbacks(this, fieldId, null)
		}
	}

	fun <T : Event> observeEvent(lifecycleOwner: LifecycleOwner, eventClass: Class<T>, eventObserver: EventObserver<T>) {
		eventBus.observe(lifecycleOwner, eventClass, eventObserver)
	}

	fun <T : Event> removeEventObservers(lifecycleOwner: LifecycleOwner, eventClass: Class<T>) {
		eventBus.removeObservers(lifecycleOwner, eventClass)
	}

	fun <T : Event> sendEvent(event: T) {
		eventBus.send(event)
	}
}