package cz.koto.misak.securityshowcase.utility

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import cz.koto.misak.securityshowcase.ContextProvider

fun showDialog(context: Context,
               @StringRes positiveButton: Int,
               @StringRes dismissButton: Int,
               @StringRes title: Int,
               @StringRes message: Int,
               callback: () -> Unit) {

    val builder = AlertDialog.Builder(context)
    builder.setCancelable(false)
    builder.setTitle(ContextProvider.getString(title))
    builder.setMessage(ContextProvider.getString(message))
    builder.setPositiveButton(ContextProvider.getString(positiveButton),
            { dialog, which -> callback.invoke() })
    builder.setNegativeButton(ContextProvider.getString(dismissButton),
            { dialog, which -> dialog.cancel() })
    builder.create().show()
}
