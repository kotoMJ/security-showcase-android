package cz.koto.misak.securityshowcase.utility

import android.databinding.ObservableField
import android.os.Build
import cz.kinst.jakub.view.SimpleStatefulLayout
import cz.kinst.jakub.view.StatefulLayout
import java.text.SimpleDateFormat
import java.util.*


fun <T> ObservableField<List<T>>.replace(action: (T) -> T) =
        this.set(this.get().map { action(it) })

fun <T> ObservableField<List<T>>.filter(action: (T) -> Boolean) =
        this.set(this.get().filter { action(it) })

fun Date.formatted() = SimpleDateFormat("MMM d", Locale.ENGLISH).format(this.time)
fun Date.formattedWithYear() = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(this.time)

fun Date.dayOfWeek() = SimpleDateFormat("EEEE", Locale.getDefault()).format(this.time)

data class SelectableItem<out T>(
        val item: T,
        val isSelected: Boolean = false)

inline fun runOnLollipop(crossinline action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= 21) action()
}

inline fun runOnMarshmallow(crossinline action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= 23) action()
}


fun ObservableField<String>.progress() = set(SimpleStatefulLayout.State.PROGRESS)
fun ObservableField<String>.offline() = set(SimpleStatefulLayout.State.OFFLINE)
fun ObservableField<String>.content() = set(StatefulLayout.State.CONTENT)
fun ObservableField<String>.empty() = set(SimpleStatefulLayout.State.EMPTY)

fun ObservableField<String>.emptyWhen(action: () -> Boolean) =
		if (action()) set(SimpleStatefulLayout.State.EMPTY) else set(StatefulLayout.State.CONTENT)

operator fun <A, B> ((A) -> B).get(a: A): () -> B = { this(a) }
