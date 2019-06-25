package cz.kotox.securityshowcase.core.ktools

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cz.kotox.securityshowcase.core.R

@BindingAdapter("android:src")
fun setImageUri(view: ImageView, imageUri: Uri) {
	view.setImageURI(imageUri)
}

@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, drawable: Drawable) {
	view.setImageDrawable(drawable)
}

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
	imageView.setImageResource(resource)
}

@BindingAdapter("app:fabEnabled")
fun setFabEnabled(view: FloatingActionButton, isEnabled: Boolean) {
	view.isEnabled = isEnabled
	view.backgroundTintList = ContextCompat.getColorStateList(view.context, if (isEnabled) R.color.global_accent else R.color.global_control_disabled)
	if (isEnabled) {
		view.colorFilter = null
	} else {
		view.setColorFilter(ContextCompat.getColor(view.context, R.color.global_text_disable), android.graphics.PorterDuff.Mode.SRC_IN)
	}
}
