package cz.koto.securityshowcase.core.arch

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import cz.koto.securityshowcase.core.arch.event.Event
import cz.koto.securityshowcase.core.arch.event.EventBus
import cz.koto.securityshowcase.core.arch.event.EventObserver

abstract class BaseViewModelLegacy : Observable, ViewModel() {
	@Transient
	private var mObservableCallbacks: PropertyChangeRegistry? = null
	private var eventBus = EventBus()

	@Synchronized
	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		if (mObservableCallbacks == null) {
			mObservableCallbacks = PropertyChangeRegistry()
		}
		mObservableCallbacks?.add(callback)
	}

	@Synchronized
	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
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