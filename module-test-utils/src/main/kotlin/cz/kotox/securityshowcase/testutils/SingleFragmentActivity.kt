package cz.kotox.securityshowcase.testutils

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cz.kotox.securityshowcase.testutils.test.R

/**
 * Used as container to test fragments in isolation with Espresso
 */
@RestrictTo(RestrictTo.Scope.TESTS)
class SingleFragmentActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val content = FrameLayout(this)
		content.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT)
		content.id = R.id.content_frame
		setContentView(content)
	}

	fun setFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.add(R.id.content_frame, fragment, "TEST")
			.commit()
	}

	fun replaceFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.content_frame, fragment).commit()
	}
}