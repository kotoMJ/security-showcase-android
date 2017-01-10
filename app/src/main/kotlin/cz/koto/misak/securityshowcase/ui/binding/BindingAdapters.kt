package cz.koto.misak.securityshowcase.ui.binding

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import cz.koto.misak.securityshowcase.R
import java.io.File


@BindingAdapter("hide")
fun setHide(view: View, hide: Boolean) {
    view.visibility = if (hide) View.GONE else View.VISIBLE
}


@BindingAdapter("show")
fun setShow(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}


@BindingAdapter("invisible")
fun setInvisible(view: View, invisible: Boolean) {
    view.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("visible")
fun setVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}


@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_goal)
            .thumbnail(0.5f)
            .dontAnimate()
            .into(imageView)
}

@BindingAdapter("imageFile")
fun setImageFromFile(imageView: ImageView, file: File) {
    Glide.with(imageView.context)
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_goal)
            .thumbnail(0.5f)
            .dontAnimate()
            .into(imageView)
}


