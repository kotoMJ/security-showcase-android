package cz.koto.securityshowcase.utility

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

sealed class ApplicationEvent {
	object RequestLogin : ApplicationEvent()
}

val applicationEvents: Subject<ApplicationEvent> = PublishSubject.create<ApplicationEvent>().toSerialized()