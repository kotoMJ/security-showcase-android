package cz.koto.misak.securityshowcase.utility

import android.view.MotionEvent
import android.widget.EditText


fun EditText.onClick(action: () -> Unit) {
    this.setOnTouchListener { v, e ->
        if (e.action == MotionEvent.ACTION_UP && !v.hasFocus())
            v.performClick()
        false
    }

    this.setOnClickListener { action() }
}