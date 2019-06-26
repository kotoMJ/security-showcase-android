package cz.kotox.securityshowcase.module_core.arch

import cz.kotox.securityshowcase.module_core.arch.event.Event
import cz.kotox.securityshowcase.module_core.arch.event.EventBus
import cz.kotox.securityshowcase.module_core.arch.event.EventObserver

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