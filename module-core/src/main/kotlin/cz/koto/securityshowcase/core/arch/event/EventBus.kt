package cz.kotox.securityshowcase.module_core.arch.event

abstract class Event

interface EventObserver<T> : Observer<T> {
	override fun onChanged(event: T?)
}

class EventBus {
	private val mEventMap: MutableMap<Class<out Event>, LiveEvent<out Event>>

	init {
		mEventMap = ArrayMap()
	}

	@SuppressWarnings("unchecked")
	fun <T : Event> observe(@NonNull lifecycleOwner: LifecycleOwner, @NonNull eventClass: Class<T>, @NonNull eventObserver: EventObserver<T>) {
		var liveEvent = mEventMap[eventClass] as LiveEvent<T>?
		if (liveEvent == null) {
			liveEvent = initLiveEvent(eventClass)
		}
		liveEvent.observe(lifecycleOwner, eventObserver)
	}

	@SuppressWarnings("unchecked")
	fun <T : Event> removeObservers(@NonNull lifecycleOwner: LifecycleOwner, @NonNull eventClass: Class<T>) {
		val liveEvent = mEventMap[eventClass] as LiveEvent<T>?
		if (liveEvent != null) {
			liveEvent.removeObservers(lifecycleOwner)
		}
	}

	@SuppressWarnings("unchecked")
	fun <T : Event> send(@NonNull event: T) {
		var liveEvent = mEventMap[event::class.java] as LiveEvent<T>?
		if (liveEvent == null) {
			liveEvent = initLiveEvent(event::class.java as Class<T>)
		}
		liveEvent.setValue(event)
	}

	@NonNull
	private fun <T : Event> initLiveEvent(eventClass: Class<T>): LiveEvent<T> {
		val liveEvent = LiveEvent<T>()
		mEventMap[eventClass] = liveEvent
		return liveEvent
	}
}
