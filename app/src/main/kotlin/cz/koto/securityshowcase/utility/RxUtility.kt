package cz.koto.securityshowcase.utility

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject


fun <A> BehaviorSubject<A>.update(step: (A) -> A) = take(1).subscribe { onNext(step(it)) }

fun <A, B> latestToPair(a: Observable<A>, b: Observable<B>) =
		a.withLatestFrom(b, BiFunction { t1: A, t2: B -> t1 to t2 })