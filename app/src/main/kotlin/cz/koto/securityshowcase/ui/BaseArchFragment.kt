package cz.koto.securityshowcase.ui

import android.support.v4.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

open class BaseArchFragment : Fragment() {

	val detached = PublishSubject.create<Unit>()

	override fun onDestroy() {
		detached.onNext(Unit)
		super.onDestroy()
	}

	protected fun <A> bind(source: io.reactivex.Observable<A>, action: (A) -> Unit) = source
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.takeUntil(detached)
			.subscribe(action)
}