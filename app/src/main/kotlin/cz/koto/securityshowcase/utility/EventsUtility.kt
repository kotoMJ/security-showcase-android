package cz.koto.securityshowcase.utility

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

sealed class ApplicationEvent {
	object RequestLogin : ApplicationEvent()
	object RequestMain : ApplicationEvent()
	object RequestStoreSecret : ApplicationEvent()
}

val applicationEvents: Subject<ApplicationEvent> = PublishSubject.create<ApplicationEvent>().toSerialized()