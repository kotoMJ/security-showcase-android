package cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import cz.kotox.keystorecompat.KeystoreCompat
import cz.kotox.keystorecompat.base.utility.showLockScreenSettings
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.security.R
import cz.kotox.securityshowcase.security.databinding.FragmentSettingsBinding
import timber.log.Timber
import javax.inject.Inject

class SettingsFragment : BaseFragmentViewModel<SettingsViewModel, FragmentSettingsBinding>() {

	@Inject
	lateinit var keystoreCompat: KeystoreCompat

	override fun setupViewModel() = findViewModel<SettingsViewModel>()

	override fun inflateBindingLayout(inflater: LayoutInflater): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		savedInstanceState?.let {
			//TODO handle automatic activation here .... based on args?
			if (it.get("EXTRA_ENCRYPTION_REQUEST_SCHEDULED") == true) {
				activateKeystoreCompat()
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = super.onCreateView(inflater, container, savedInstanceState)

		binding.lockScreenLayout.lockScreenSettingsLink.setOnClickListener {
			showLockScreenSettings(context)
		}

		binding.lockScreenLayout.lockScreenSwitch.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				activateKeystoreCompat()
			} else {
				keystoreCompat.deactivate()
			}
		}

		setupEnrollmentProgressView()

		return view
	}

	private fun setupEnrollmentProgressView() {
		binding.lockScreenLayout.progressBar.visibility = View.GONE
		viewModel.enrollmentInProgress.observe(this, Observer<Boolean> { progress ->
			Timber.w(">>> progress[${progress}]")
			if (progress == true) {
				binding.lockScreenLayout.progressBar.visibility = View.VISIBLE
				binding.lockScreenLayout.lockScreenSwitch.isEnabled = false
			} else {
				binding.lockScreenLayout.lockScreenSwitch.isChecked = keystoreCompat.hasSecretLoadable()
				binding.lockScreenLayout.progressBar.visibility = View.GONE
				updateView()
			}
		})
	}

	private fun activateKeystoreCompat() {
		viewModel.enrollKeystoreCompat { intent ->
			baseActivity.startActivityForResult(intent, 99)
		}
	}

	override fun onResume() {
		super.onResume()
		updateView()
	}

	private fun updateView() {
		@Suppress("MagicNumber")
		binding.lockScreenLayout.lockScreenLabelKitkatDisclaimer.visibility =
			if (Build.VERSION.SDK_INT == 19) View.VISIBLE else View.GONE

		val securityEnabled = keystoreCompat.isSecurityEnabled()
		val securitySettingsDisclaimer: Int
		val securitySettingsLinkText: Int

		when {
			securityEnabled -> {
				securitySettingsDisclaimer = R.string.settings_security_action_update
				binding.lockScreenLayout.lockScreenSettingsDisclaimer.typeface = Typeface.DEFAULT
				securitySettingsLinkText = R.string.settings_security_link_android_security_update
			}
			else -> {
				securitySettingsDisclaimer = R.string.settings_security_action_enable
				binding.lockScreenLayout.lockScreenSettingsDisclaimer.setTypeface(binding.lockScreenLayout.lockScreenSettingsDisclaimer.typeface, Typeface.BOLD)
				securitySettingsLinkText = R.string.settings_security_link_android_security_enable
			}
		}

		binding.lockScreenLayout.lockScreenSwitch.isEnabled = securityEnabled
		binding.lockScreenLayout.lockScreenSwitchLabel.isEnabled = securityEnabled
		binding.lockScreenLayout.lockScreenSwitchLabel.alpha = if (securityEnabled) 1f else 0.5f

		binding.lockScreenLayout.lockScreenSettingsDisclaimer.setText(securitySettingsDisclaimer)
		binding.lockScreenLayout.lockScreenSettingsLink.setText(securitySettingsLinkText)
	}
}