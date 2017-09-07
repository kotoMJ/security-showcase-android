package cz.koto.securityshowcase

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat

object ContextProvider {
	lateinit var context: Context

	fun initialize(context: Context) {
		this.context = context
	}

	fun getResources() = context.resources


	fun getString(@StringRes resourceId: Int) = getResources().getString(resourceId)


	fun getString(@StringRes resourceId: Int, vararg args: Any) = getResources().getString(resourceId, *args)


	fun getColor(@ColorRes resourceId: Int) = ContextCompat.getColor(context, resourceId)
}
