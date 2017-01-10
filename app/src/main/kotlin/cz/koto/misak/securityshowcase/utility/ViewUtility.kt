package cz.koto.misak.securityshowcase.utility

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.view.View

inline fun View.onClick(crossinline action: () -> Unit) = setOnClickListener { action() }

fun View.showMenuOnClick(resource: Int, actions: (Int) -> Unit) =
        PopupMenu(context, this).run {
            inflate(resource)
            setOnMenuItemClickListener { actions(it.itemId); true }
            show()
        }

inline fun <reified A : Activity> View.start() = context.startActivity(Intent(context, A::class.java))

inline fun <reified A : Activity> View.start(config: Intent.() -> Unit) =
        context.startActivity(Intent(context, A::class.java).apply(config))