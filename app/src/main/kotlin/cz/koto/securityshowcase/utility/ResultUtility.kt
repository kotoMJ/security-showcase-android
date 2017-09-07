package cz.koto.securityshowcase.utility


sealed class State<out E, out V> {
	object Initial : State<Nothing, Nothing>()
	object Fetching : State<Nothing, Nothing>()
	class Value<out V>(val value: V) : State<Nothing, V>() {
		override fun toString(): String = "State,Value: $value"
	}

	class Error<out E>(val error: E) : State<E, Nothing>() {
		override fun toString(): String = "State,Error: $error"
	}
}

fun <A> value(value: A): State<Nothing, A> = State.Value(value)
fun <A> error(value: A): State<A, Nothing> = State.Error(value)